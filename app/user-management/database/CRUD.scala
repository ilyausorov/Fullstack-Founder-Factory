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
 * Basic CRUD queries for extension
 * @tparam T
 * @tparam A
 */
trait Queries[T <: IndexedTable[A], A <: Unique] {

	val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

	val db = dbConfig.db
	val query: TableQuery[T]

	val listQuery   = (f: T => Rep[Boolean])     => query.filter(f).result
	val detailQuery = (f: T => Rep[Boolean])     => query.filter(f).result.headOption
	val deleteQuery = (f: T => Rep[Boolean])     => query.filter(f).delete

    val insertQuery = (v: A)                     => (query returning query.map(_.id)) += v
	val updateQuery = (f: T => Rep[Boolean],v:A) => v.id.map(id =>query.filter(f).update(v).map(_ => id)).getOrElse{insertQuery(v)}

    val lastQuery   = (f: T => Rep[Boolean])     => query.filter(f).sortBy(_.id).result.headOption

    lazy val tableName:String   = query.baseTableRow.tableName
}

/**
 *CRUD stands for
 * C: Create
 * R: Read
 * U: Update
 * D: Delete
 * @tparam T Table
 * @tparam A Row
 */
trait CRUD[T <: IndexedTable[A], A <: Unique] extends Queries[T,A] {

  def numRowQuery = query.length.result

  def list:Future[Seq[A]] = db.run{query.result}

  def detail(id:Long):Future[Option[A]] = db.run{detailQuery(_.id === id)}

  def delete(id:Long):Future[Int] = db.run{deleteQuery(_.id === id)}

  def update(v:A):Future[Long] = db.run{updateQuery(_.id === v.id.get, v)}

	def updateAll(elements:Seq[A]) = db.run{
			DBIO.seq(elements.map(e => updateQuery(_.id === e.id.get,e)): _*).transactionally
	}

  def last:Future[Option[A]] = db.run{query.result.headOption}

  lazy val numRow:Future[Int] = db.run{numRowQuery}
}

/**
 * same as CRUD but with position
 * watch out for concurrency problems!! 
 */
trait CRUDPosition[T <: IndexedTableP[A], A <: UniquePosition] extends CRUD[T,A]{

  def listPositionQuery = query.sortBy(_.position).result

  def detailByPositionQuery(position: Long) = detailQuery(_.position === position)

  def updatePositionSimpleQuery(id: Long, position: Long) = query.filter(_.id === id).map(_.position).update(position)

  def updatePositionQuery(v: A) = for{
    d <- detailQuery(_.id === v.id.get)
    u <- updateQuery(_.id === v.id.get,v)
    n <- numRowQuery
    e <- query.filter(_.id === v.id.get)
          .map(_.position)
          .update(d.get.position)
  } yield(u)

  def insertPositionQuery(v: A):slick.dbio.DBIOAction[Int,slick.dbio.NoStream,slick.dbio.Effect.Write with slick.dbio.Effect.Read with slick.dbio.Effect.Write] = for{
    u <- (query returning query.map(_.id)) += v
    n <- numRowQuery
    e <- query.filter(_.id === u).map(_.position).update(n+1)
  } yield(e)

  def deletePositionQuery(id: Long) = for{
    d <- detailQuery(_.id === id)
    e <- deleteQuery(_.id === id)
    f <- d.map{de => listQuery(_.position >= de.position)}.getOrElse{null}  // todo: get rid of `null`, not too nice
    _ <- DBIO.seq(f.map{g => query.filter(_.id === g.id.get).map(_.position).update(g.position -1)}:_*)      
  } yield (e)

  def moveQuery(id: Long, delta: Int) = for{
    a <- detailQuery(_.id === id)
    b <- a.map{d=> for{
        a <- detailByPositionQuery(d.position - delta)
        b <- a.map{o =>
          updatePositionSimpleQuery(d.id.get, o.position)
          updatePositionSimpleQuery(o.id.get, o.position - delta)
        }.getOrElse(null)
      } yield(a)
    }.getOrElse(null)
  } yield(a)

  override def list:Future[Seq[A]]                       = db.run(listPositionQuery)
  override def update(v:A):Future[Long]                  = {db.run(v.id.map{id =>
      updatePositionQuery(v)
    }
    .getOrElse{
      insertPositionQuery(v)
    }.transactionally)
    Future{1l}
  } // make sure this is a real transaction!!
  override def delete(id: Long):Future[Int]              = db.run(deletePositionQuery(id).transactionally);Future(1) // todo take from previous result
  def detailByPosition(position: Long):Future[Option[A]] = db.run(detailByPositionQuery(position))
  def move(id: Long, delta: Int = 1)                     = db.run(moveQuery(id, delta).transactionally) 
}