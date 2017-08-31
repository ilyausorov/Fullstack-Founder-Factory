package user_management.user.database

import user_management.user.actions.UserRequest
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc.AnyContent
import slick.dbio.DBIO
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

/**
 * CRUD on a company basis
 * @tparam T
 * @tparam A
 */
trait CRUDByCompany[T <: IndexedTableCompany[A], A <: UniqueCompany] extends Queries[T,A]{

  def list(company_id: Long):Future[Seq[A]]                                         = db.run{listQuery(x    => x.company_id === company_id)}
  def detail(id: Long, company_id: Long):Future[Option[A]]                          = db.run{detailQuery(x  => x.company_id === company_id && x.id=== id)}
  def delete(id: Long, company_id: Long):Future[Int]                                = db.run{deleteQuery(x  => x.company_id === company_id && x.id=== id)}
  def deleteAll(company_id: Long):Future[Int]                                       = db.run{deleteQuery(x  => x.company_id === company_id)}
  def update(v:A, company_id: Long):Future[Long]                                    = db.run{updateQuery(x  => x.company_id === company_id && x.id === v.id.get,v)}
  def numRow(company_id: Long):Future[Int]                                          = db.run{query.filter(x => x.company_id === company_id).length.result}
  def last(company_id: Long):Future[Option[A]]                                      = db.run(lastQuery(x    => x.company_id === company_id))

  def list(implicit request:UserRequest[AnyContent]):Future[Seq[A]]                 = list(request.user.company_id)
  def detail(id: Long)(implicit request:UserRequest[AnyContent]):Future[Option[A]]  = detail(id, request.user.company_id)
  def update(v:A)(implicit request:UserRequest[AnyContent]):Future[Long]            = update(v, request.user.company_id)
  def delete(id: Long)(implicit request:UserRequest[AnyContent]):Future[Int]        = delete(id, request.user.company_id)
  def deleteAll(implicit request:UserRequest[AnyContent]):Future[Int]               = deleteAll(request.user.company_id)
  def numRow(implicit request:UserRequest[AnyContent]):Future[Int]                  = numRow(request.user.company_id)
}

/**
 * CRUD model for 1:1 relationships
 */
trait CRUDByCompany11[T <: IndexedTableCompany[A], A <: UniqueCompany] extends CRUDByCompany[T,A]{
  def listSingle(company_id: Long):Future[Option[A]] = db.run{detailQuery(_.company_id === company_id)}

  override def update(v:A, company_id: Long):Future[Long] = {
    deleteAll(company_id).flatMap{f =>
      db.run(insertQuery(v))
    }
  }
}