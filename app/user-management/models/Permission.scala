package user_management.user.models

import javax.inject.Inject

import user_management.user.actions.UserRequest
import user_management.user.database.{CRUDByUser, CRUD, UniqueUser, Unique}
import user_management.user.{UserPermissionDB, PermissionDB}
import play.api.cache.CacheApi
import play.api.mvc.AnyContent
import slick.lifted.TableQuery
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Permission(id: Option[Long], name: String, assigned: Boolean = false) extends Unique {
  def toUserPermissionRow(implicit user:User) = UserPermissionRow(None,user.id.get,id.get)
}

trait Permissions{
  val permissions:Seq[Long]
}

object Permission extends CRUD[PermissionDB, Permission]{
  val query = TableQuery[PermissionDB]

  lazy val Permissions = Seq(Admin,User)

  val Admin    = 1l // detail("admin").map{x =>    x.id.get}.getOrElse(1l)
  val User     = 2l // detail("user").map{x =>     x.id.get}.getOrElse(2l)

  def detail(name: String):Future[Option[Permission]] = list.map{a => a.filter(_.name == name).headOption}
}

case class UserPermissionRow(id: Option[Long], user_id: Long, permission_id: Long) extends UniqueUser

class UserPermission @Inject()(cache: CacheApi) extends CRUDByUser[UserPermissionDB, UserPermissionRow] {

  val query = TableQuery[UserPermissionDB]

  def listWithAssign(id: Long) = {
    super.list(id).flatMap{up =>
      Permission.list.map{fx =>
        fx.map{x =>
          val assign = up
            .filter(_.permission_id == x.id.get)
            .headOption
            .isDefined
          x.copy(assigned = assign)
        }
      }
    }
  }

  def filter(up: UserPermissionRow): UserPermissionDB => Rep[Boolean] = {r => r.user_id === up.user_id && r.permission_id === up.permission_id}

  def assign(permission: Permission)(implicit user: User) = db.run{

    val up = permission.toUserPermissionRow

    query.filter(filter(up)).exists.result.flatMap { exists =>
      if (!exists) updateQuery(_.user_id === up.user_id, up)
      else         DBIO.successful(0L)
    }.transactionally

  } andThen { case worked => cache.remove(Constants.cacheKey + user.username) }

  def revoke(permission: Permission)(implicit user: User) = db.run{
    val up = permission.toUserPermissionRow
    
    query.filter(filter(up)).delete

  } andThen { case successful => cache.remove(Constants.cacheKey + user.username) }
}