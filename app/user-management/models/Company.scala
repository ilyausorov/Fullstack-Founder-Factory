package user_management.user.models

import user_management.user.actions.UserRequest
import user_management.user.database._

import javax.inject.Inject

//import user_management.user.models.UserCompany
import play.api.mvc.AnyContent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import slick.driver.MySQLDriver.api._

import user_management.user.{CompanyDB, UserCompanyDB}
import user_management.user.database.{Unique, CRUD}
import user_management.user.database.{UniqueUser, CRUDByUser}

import org.joda.time.DateTime

import slick.lifted.TableQuery

case class CompanyRow(
  id: Option[Long],
  name: String,
  domain: Option[String] = None,
  date_added:Option[DateTime] = None
) extends Unique

object Company extends CRUD[CompanyDB, CompanyRow]{
	val query = TableQuery[CompanyDB]
}

case class UserCompanyRow(
  id: Option[Long],
  user_id: Long,
  company_id: Long,
  role: Option[String] = None
) extends UniqueUser

class UserCompany extends CRUDByUser[UserCompanyDB, UserCompanyRow]{
  val query = TableQuery[UserCompanyDB]

  /**
   * since this table can be fetched with either by user (default, use `list` for this) or company_id, this function is for the latter
   * @param  company_id
   * @return Future[Seq[UserCompanyRow]]
   */
  def listByCompany(company_id: Long):Future[Seq[UserCompanyRow]] = db.run{query.result}.map{a => a.filter(_.company_id == company_id)}

  /**
   * Remove a user-company combination
   * @param company_id
   * @param user_id
   * @return
   */
  def removeUserCompany(company_id:Long,user_id:Long) = db.run{

    query.filter(uc => uc.user_id === user_id && uc.company_id === company_id).delete

  }

  def removeUserCompany(company_id:Long)(implicit user:UserRequest[AnyContent]):Future[Int] = removeUserCompany(company_id,user.id)

}