package user_management.user

import javax.inject.Inject

import _root_.database.user.models.NewUser
import akka.actor.Status.Success
import utilities.encryption.Md5
import user_management.user.models._
import play.api.cache.CacheApi
import slick.dbio.DBIO
import slick.driver.MySQLDriver.api._
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, duration}
import scala.language.postfixOps

class UserManagementService @Inject()(
  cache:CacheApi,
  userPermission:UserPermission,
  userCompanyService: UserCompany
) extends UserManagement[UserDB, UserRow] {
  val query = TableQuery[UserDB]

  /* LIST */
  def listByCompany(company_id: Long) = db.run {
    query.filter(_.company_id === company_id).result
  }

  def listByPermission(permission_id:Long) = db.run{
    query.flatMap(u => userPermission.query.filter(p => p.user_id === u.id && p.permission_id === permission_id ).map(p => (u))).result
  }

  /* FIND */
  def findRestrictedUser(username: String) = cache.getOrElse(Constants.cacheKey + username, duration.DurationInt(4 * 3600) seconds) {
    findRestrictedUserGeneric(_.username.toLowerCase === username.toLowerCase)
  }

  def findRestrictedUserByToken(token: String)       = findRestrictedUserGeneric(_.md5Password === utilities.encryption.Md5.hash(token))
  
  def findRestrictedUserByKey(id:Long,key: String)   = findRestrictedUserGeneric(u => u.id === id &&u.key === key)

  private def findRestrictedUserGeneric(f: UserDB => Rep[Boolean]):Future[Option[RestrictedUser]]={
    val statement = query.filter(f).flatMap(u => userPermission.query.filter(_.user_id === u.id).map(p => (u,p))).result

    db run statement map(list =>
      list.headOption.map{case (user,permission) =>
        PermittedUser(user.id,user.company_id, user.username, user.bcryptedPassword, user.md5Password, user.key, user.status_id,list.map(_._2.permission_id))
      }
    )
  }

  /* UPDATE */
  override def update(v:UserRow):Future[Long] = db.run{
    updateQuery(_.id === v.id.get,v)} andThen {case successful => removeCache(v.username)
  }

  override def changeStatus(id: Long, key: String, status:Long): Future[Boolean]  = db.run{
    query.filter(user => user.id === id && user.key === key).map(u => u.status_id).update(status).map(_ > 0)} andThen {case successful => removeCache(id)
  }

  override def changePassword(id: Long, newPassword: String, resetKey: Boolean = true): Future[Int] = db.run{

    lazy val passwordToUpdate =Password.hash(newPassword)

    if(resetKey) query.filter(_.id === id).map(user => (user.bcryptedPassword,user.key)).update((passwordToUpdate,Secret.randomKey))
    else         query.filter(_.id === id).map(user => user.bcryptedPassword).update(passwordToUpdate)

  } andThen {case successful => removeCache(id)}

                
  override def changeWebServicePassword(id: Long, newPassword: String, resetKey: Boolean = true): Future[Int]  = db.run{

    lazy val passwordToUpdate = Md5.hash(newPassword)

    if (resetKey) query.filter(_.id === id).map(user => (user.md5Password,user.key)).update((passwordToUpdate,Secret.randomKey))
    else query.filter(_.id === id).map(user => user.md5Password).update(passwordToUpdate)

  }  andThen {case successful => removeCache(id)}

  override def forgotPassword(username: String, emailSender: (User, String) => Unit): Future[Any] = db.run{

    val key = Secret.randomKey

    query.filter(_.username.toLowerCase === username.toLowerCase).map(user => user.key).update(key).map(result =>
      if (result > 0) find(username).map(_.foreach(user => emailSender(user,key)))
    )

  } andThen {case successful => removeCache(username)}

  def passwordReinit(id:Long,key: String, password: String):Future[Int]={
    findRestrictedUserByKey(id,key).flatMap{u =>
      u.map{u =>
        changePassword(u.id.get, password, resetKey = true) andThen {case successful => successful.map(user => removeCache(u.id.get))}
      }
      .getOrElse(Future(0))
    }
  }

  def removeCache(username:String) = cache.remove(Constants.cacheKey + username)
  def removeCache(id:Long) = detail(id).map(_.foreach(user => cache.remove(Constants.cacheKey + user.username)))


  val insertPermissions = (permissions:Seq[Long], user_id:Long) => permissions.map(permission_id => UserPermissionRow(None,user_id,permission_id)).map{p => 
    {(userPermission.query returning userPermission.query.map(_.id)) += p}
  }

  /**
   * reset key
   * @param id: user id
   */
  def resetKey(id: Long) = db.run{
    query.filter(_.id === id).map(user => user.key).update(Secret.randomKey)
  }

  /**
   * Add a new user without a corresponding company
   * need to already have a company!
   * @param signUp
   */
  override def add(signUp: NewUser):Future[RestrictedUser] = {

    lazy val userRow = signUp.toRow

    val transaction =
      {
        {(query returning query.map(_.id)) += userRow}.flatMap(user_id => {
          DBIO.seq(insertPermissions(signUp.permissions,user_id): _*).flatMap(_ => DBIO.successful(
            PermittedUser(Some(user_id),userRow.company_id,userRow.username,userRow.bcryptedPassword,userRow.md5Password,userRow.key,userRow.status_id,signUp.permissions)
          ))
        })

      }.transactionally

    db run transaction andThen {case successful => successful.map(user => removeCache(user.username))}

  }

  /**
   * Add a new user with a corresponding company
   * @param signUp
   * @param company
   */
  override def add(company: CompanyRow, signUp: NewUser, position: Option[String]): Future[RestrictedUser] = {

    lazy val userRow = signUp.toRow

    val transaction =
      {
        // 1. create company
        {(Company.query returning Company.query.map(_.id)) += company}.flatMap(company_id => {
        // 2. create user
          {(query returning query.map(_.id)) += userRow.copy(company_id = company_id)}.flatMap{user_id => 

        // 3, create n-n relationship for companies
            {userCompanyService.query += UserCompanyRow(None, user_id, company_id, position)}.flatMap{uc_id=>

        // 4. insert permissions
              DBIO.seq(insertPermissions(signUp.permissions,user_id): _*).flatMap(_ => DBIO.successful(
                PermittedUser(Some(user_id),company_id,userRow.username,userRow.bcryptedPassword,userRow.md5Password,userRow.key,userRow.status_id,signUp.permissions)
              ))
            }

          }

        })

      }.transactionally

    db run transaction andThen {case successful => successful.map(user => removeCache(user.username))}

  }

  /**
   * Add a user with company and user extended information
   * @param company
   * @param signUp
   * @param userExtended
   * @param position
   * @return
   */
  def add(company: CompanyRow, signUp: NewUser,userExtended:UserExtRow,position:Option[String]): Future[RestrictedUser] = {

    lazy val userRow = signUp.toRow

    val transaction =
      {
        // 1. create company
        {(Company.query returning Company.query.map(_.id)) += company}.flatMap(company_id => {
          // 2. create user
          {(query returning query.map(_.id)) += userRow.copy(company_id = company_id)}.flatMap{user_id =>

            val insertUserExtended = {UserExt.query += userExtended.copy(id = Option(user_id),user_id=user_id)}
            val insertUserCompany = {userCompanyService.query += UserCompanyRow(None, user_id, company_id, position)}

            // 3. insert permissions and extended user

            DBIO.seq(Seq(insertUserExtended,insertUserCompany) ++ insertPermissions(signUp.permissions,user_id): _*).flatMap(_ => DBIO.successful(
              PermittedUser(Some(user_id),company_id,userRow.username,userRow.bcryptedPassword,userRow.md5Password,userRow.key,userRow.status_id,signUp.permissions)
            ))

          }

        })

      }.transactionally

    db run transaction andThen {case successful => successful.map(user => removeCache(user.username))}

  }

  override def changeUsername(user_id: Long,userName: String): Future[Boolean] = db.run{
    query.filter(_.id === user_id).map(_.username).update(userName).map(_ > 0)
  } andThen {case successful => removeCache(userName)}

  override def delete(user_id:Long) = db.run{deleteQuery(_.id === user_id)} andThen {case successful => removeCache(user_id)}

  def listByCompanyFull(company_id: Long):Future[Seq[(UserCompanyRow, UserRow)]] = {
    list.flatMap{users =>
      userCompanyService.listByCompany(company_id).map{uc =>
        users.map{x => 
          uc.filter(_.user_id == x.id.get).headOption.map{y =>
            Some(y, x)
          }
          .getOrElse(None)
        
        }.flatten[(UserCompanyRow, UserRow)]
      }
    }
  }
}