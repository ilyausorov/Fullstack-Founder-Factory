package user_management.user.models

import user_management.user.{CompanyFileDB}
import user_management.user.database.{UniqueCompany, CRUDByCompany}

import org.joda.time.DateTime

import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class CompanyFile(
  id: Option[Long]
  , company_id: Long
  , key: String
  , name: String
  , contentType: Option[String] = None
  , type_id: Long = 1l
  , date: org.joda.time.DateTime= new org.joda.time.DateTime
) extends UniqueCompany

object CompanyFile extends CRUDByCompany[CompanyFileDB, CompanyFile]{

  val query = TableQuery[CompanyFileDB]

  def filename(id: Long) = utilities.file.File.name(id, tableName)

  /**
   * @returns last entr
   * @param user_id
   * @param type_id
   */
  def last(company_id: Long, type_id: Long = 1):Future[Option[CompanyFile]] = list(company_id).map{f =>
    f.sortBy(_.id).filter(_.type_id == 1).headOption
  }
}