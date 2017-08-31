package user_management.user

import _root_.database.user.models.NewUser
import user_management.user.actions.UserRequest
import user_management.user.database.{CRUD, IndexedTableUserManagement}
import user_management.user.models.{UserExtRow, RestrictedUser, CompanyRow, User}

import play.api.mvc.AnyContent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait UserManagement[T <:IndexedTableUserManagement[A], A <:User] extends CRUD[T,A]{
  import slick.driver.MySQLDriver.api._
  val findQuery = (username:String) => query.filter(_.username.toLowerCase === username.toLowerCase).result.headOption

  /**
   * Add a new user without a corresponding company
   * @param credentials
   * @return Id of newly added user
   */
  def add(credentials:NewUser):Future[RestrictedUser]

  /**
   * Add a new user with a corresponding company
   * @param company
   * @param credentials
   * @param role
   * @return Id of newly added user
   */
  def add(company:CompanyRow, credentials:NewUser, role: Option[String] = None):Future[RestrictedUser]

  /**
   * Add a company with an extended user
   * @param company
   * @param credentials
   * @param userExtended
   * @return
   */
  def add(company: CompanyRow, credentials: NewUser,userExtended:UserExtRow, role: Option[String]):Future[RestrictedUser]

  /**
   * Check combinUnitn of id and key
   * @param id
   * @param key
   * @return
   */
  def checkIdAndKey(id: Long, key: String):Future[Boolean] = detail(id).map{_.exists(_.key == key)}


  /**
   * Find a user by email
   * @param username
   * @return
   */

  def find(username: String):Future[Option[A]] = db.run{findQuery(username)}

  def findRestrictedUser(username:String):Future[Option[RestrictedUser]]

  def findRestrictedUserByToken(token: String): Future[Option[RestrictedUser]]

  /**
   * Authenticate a user by status and password
   * @param username
   * @param password
   * @return
   */
  def authenticate(username:String, password: String): Future[Option[RestrictedUser]] = {

    findRestrictedUser(username).map{_.flatMap{
      user => if ( (user.status_id == user_management.user.Status.ok || user.status_id == user_management.user.Status.emailNotConfirmed)  && Password.check(user.bcryptedPassword,password)) Some(user) else None}}
  }

  def authenticateWebService(username:String,password:String):Future[Option[RestrictedUser]] = {
    findRestrictedUser(username).map{_.flatMap{user => if ((user.status_id == Status.ok || user.status_id == user_management.user.Status.emailNotConfirmed) && Password.checkMd5(user.md5Password,password)) Some(user) else None}}
  }

  /**
   * Activate a user
   * @param id
   * @param key
   * @return
   */
  def activate(id: Long, key: String) = changeStatus(id,key, user_management.user.Status.ok)

  def deactivate(id: Long, key: String) = changeStatus(id,key, user_management.user.Status.deactivated)

  def emailChange(id: Long, key: String) = changeStatus(id,key, user_management.user.Status.emailNotConfirmed)
  /**
   * Change the status of a user
   * @param id
   * @param key
   * @param status
   * @return
   */
  def changeStatus(id:Long,key:String, status:Long): Future[Boolean]


  /**
   * Check if username is already taken. Note that if we are modifying the current entity, the function should include the result of the current entity!
   * @param email
   * @param id
   * @return
   */
  def exists(email: String, id: Option[Long] = None): Future[Boolean] = find(email).map{someUser =>
    someUser.exists{
      user => id.map{id => id != user.id.get}.getOrElse(true)
    }
  }

  /**
   * change password when key and id are given
   * @param id: user_id
   * @param key: user key
   * @param password: new password (not encrypted)
   * @param change(): function that makes the change in DB
 **/
  def changeForgot(id: Long, key: String, password: String, change: (Long, String) => Option[Long]):Future[Option[Long]] = checkIdAndKey(id, key).map{x => if(x){change(id, password)}else None}

  def sendActivationEmail(emailSender: => Unit) = emailSender

  def changePassword(id:Long,newPassword:String,resetKey:Boolean = true): Future[Int]

  def changeWebServicePassword(id: Long, newPassword: String, resetKey: Boolean = true): Future[Int]

  def forgotPassword(username:String,emailSender: (User,String) => Unit):Future[Any]

  def checkPassword(password:String,isWebServicePassword:Boolean = false)(implicit userRequest: UserRequest[AnyContent]):Future[Boolean] =
    findRestrictedUser(userRequest.user.username).map(_.exists(user => if (isWebServicePassword) Password.checkMd5(user.md5Password,password) else Password.check(user.bcryptedPassword,password)))

  def changeUsername(user_id:Long,userName:String):Future[Boolean]
}




