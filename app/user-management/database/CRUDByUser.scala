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
 * CRUD on a user basis
 * @tparam T
 * @tparam A
 */
trait CRUDByUser[T <: IndexedTableUser[A], A <: UniqueUser] extends Queries[T,A]{

  def numRowQuery                   = query.length.result
  def numRowSubQuery(user_id: Long) = query.filter(x => x.user_id === user_id).length.result

  def list(user_id: Long):Future[Seq[A]]                                            = db.run{listQuery(x   => x.user_id === user_id)}
  def detail(id: Long, user_id: Long):Future[Option[A]]                             = db.run{detailQuery(x => x.user_id === user_id && x.id === id)}
  def update(v:A, user_id: Long):Future[Long]                                       = db.run{updateQuery(x => x.user_id === user_id && x.id === v.id.get, v)}
  def delete(id: Long, user_id: Long):Future[Int]                                   = db.run{deleteQuery(x => x.user_id === user_id && x.id === id)}
  def deleteAll(user_id: Long):Future[Int]                                          = db.run{deleteQuery(x => x.user_id === user_id)}
  def last(user_id: Long):Future[Option[A]]                                         = db.run(lastQuery(x   => x.user_id === user_id))
  
  def numRow:Future[Int]                                                            = db.run(numRowQuery)
  def numRowSub(user_id: Long):Future[Int]                                          = db.run(numRowSubQuery(user_id))
  
  def list(implicit request:UserRequest[AnyContent]):Future[Seq[A]]                 = list(request.user.id.get)
  def detail(id: Long)(implicit request:UserRequest[AnyContent]):Future[Option[A]]  = detail(id, request.user.id.get)
  def update(v:A)(implicit request:UserRequest[AnyContent]):Future[Long]            = update(v, request.user.id.get)
  def delete(id: Long)(implicit request:UserRequest[AnyContent]):Future[Int]        = delete(id, request.user.id.get)
  def deleteAll(implicit request:UserRequest[AnyContent]):Future[Int]               = deleteAll(request.user.id.get)
  def numRowSub(implicit request:UserRequest[AnyContent]):Future[Int]               = numRowSub(request.user.id.get)
}

/**
 * CRUD model for 1:1 relationships
 */
trait CRUDByUser11[T <: IndexedTableUser[A], A <: UniqueUser] extends CRUDByUser[T,A]{
  def listSingle(user_id: Long):Future[Option[A]] = db.run{detailQuery(_.user_id === user_id)}

  override def update(v:A, user_id: Long):Future[Long] = {
      
    val statement = deleteQuery(_.user_id === user_id).flatMap(_ => insertQuery(v)).transactionally
    db run statement

  }
}

/**
 * todo: test this one formally
 */
trait CRUDByUserPosition[T <:IndexedTableUserP[A], A<: UniqueUserPosition] extends CRUDByUser[T,A]{

  def listPositionQuery(user_id: Long)                     = query.filter(x => x.user_id === user_id).sortBy(_.position.desc.nullsFirst).result
  def detailByPositionQuery(position: Long, user_id: Long) = detailQuery(x  => x.user_id === user_id && x.position === position)
  def updatePositionSimpleQuery(id: Long, position: Long, user_id: Long) = query.filter(x => x.user_id === user_id && x.id === id).map(_.position).update(position)

  def updatePositionQuery(v: A, user_id: Long):slick.dbio.DBIOAction[Int,slick.dbio.NoStream,slick.dbio.Effect.Write with slick.dbio.Effect.Read with slick.dbio.Effect.Write] = for{
    d <- detailQuery(x => x.user_id === user_id && x.id === v.id.get)
    u <- updateQuery(x => x.user_id === user_id && x.id === v.id.get,v)
    n <- numRowSubQuery(user_id)
    e <- query.filter(x => x.user_id === user_id && x.id === v.id.get)
          .map(_.position)
          .update(d.get.position)
  } yield(e)

  def insertPositionQuery(v: A, user_id: Long):slick.dbio.DBIOAction[Int,slick.dbio.NoStream,slick.dbio.Effect.Write with slick.dbio.Effect.Read with slick.dbio.Effect.Write] = for{
    u <- (query returning query.map(_.id)) += v
    n <- numRowSubQuery(user_id)
    e <- query.filter(x => x.user_id === user_id && x.id === u).map(_.position).update(n+1)
  } yield(e)

  def moveQuery(id: Long, delta: Int, user_id: Long) = for{
    a <- detailQuery(x => x.user_id === user_id && x.id === id)
    b <- a.map{d=> for{
        a <- detailByPositionQuery(d.position - delta, user_id)
        b <- a.map{o =>
          updatePositionSimpleQuery(d.id.get, o.position, user_id)
          updatePositionSimpleQuery(o.id.get, o.position - delta, user_id)
        }.getOrElse(null)
      } yield(a)
    }.getOrElse(null)
  } yield(a)

  def deletePositionQuery(id: Long, user_id: Long) = for{
    d <- detailQuery(x => x.user_id === user_id && x.id === id)
    e <- deleteQuery(x => x.user_id === user_id && x.id === id)
    f <- d.map{de => listQuery(x => x.user_id === user_id && x.position >= de.position)}.getOrElse(null) // todo: get rid of `null`, not too nice
    _ <- DBIO.seq(f.map{g => query.filter(x => x.user_id === user_id && x.id === g.id.get).map(_.position).update(g.position -1)}:_*)      
  } yield (e)

  override def list(user_id: Long):Future[Seq[A]]        = db.run(listPositionQuery(user_id))
  override def update(v: A, user_id: Long):Future[Long]  = {db.run(v.id.map{id =>
      updatePositionQuery(v, user_id)
    }
    .getOrElse{
      insertPositionQuery(v, user_id)
    }.transactionally)
    Future{1l}
  }

  override def delete(id: Long, user_id: Long)  = db.run(deletePositionQuery(id, user_id).transactionally)
  def detailByPosition(id: Long, user_id: Long) = db.run(detailByPositionQuery(id, user_id).transactionally)
  def move(id: Long, delta: Int, user_id: Long) = db.run(moveQuery(id, delta, user_id).transactionally)
}

/**
 * todo: test this one (not really finished)
 */
trait CRUDByUserPositionType[T <:IndexedTableUserPT[A], A<: UniqueUserPositionType] extends CRUDByUserPosition[T,A]{

  def numRowSubQuery(user_id: Long, type_id: Long) = query.filter(x => x.user_id === user_id && x.type_id === type_id).length.result

  def detailByPositionQuery(position: Long, user_id: Long, type_id: Long) = detailQuery(x  => x.user_id === user_id && x.type_id === type_id && x.position === position)
  
  def insertPositionQuery(v: A, user_id: Long, type_id: Long):slick.dbio.DBIOAction[Int,slick.dbio.NoStream,slick.dbio.Effect.Write with slick.dbio.Effect.Read with slick.dbio.Effect.Write] = for{
    u <- (query returning query.map(_.id)) += v
    n <- numRowSubQuery(user_id, type_id)
    e <- query.filter(x => x.user_id === user_id && x.type_id === type_id && x.id === u).map(_.position).update(n+1)
  } yield(e)

  def moveQuery(id: Long, delta: Int, user_id: Long, type_id: Long) = for{
    a <- detailQuery(x => x.user_id === user_id && x.type_id === type_id && x.id === id)
    b <- a.map{d=> for{
        a <- detailByPositionQuery(d.position - delta, user_id, type_id)
        b <- a.map{o =>
          updatePositionSimpleQuery(d.id.get, o.position, user_id)
          updatePositionSimpleQuery(o.id.get, o.position - delta, user_id)
        }.getOrElse(null)
      } yield(a)
    }.getOrElse(null)
  } yield(a)

  override def deletePositionQuery(id: Long, user_id: Long) = for{
    d <- detailQuery(x => x.user_id === user_id && x.id === id)
    e <- deleteQuery(x => x.user_id === user_id && x.id === id)
    f <- d.map{de => listQuery(x => x.user_id === user_id && x.type_id === de.type_id && x.position >= de.position)}.getOrElse{null}  // todo: get rid of `null`, not too nice
    _ <- DBIO.seq(f.map{g => 
      query.filter(x => x.user_id === user_id && x.type_id === g.type_id && x.id === g.id.get).map(_.position).update(g.position -1)}:_*)      
  } yield (e)

  
  def update(v: A, user_id: Long, type_id: Long):Future[Long]  = {db.run(v.id.map{id =>
      updatePositionQuery(v, user_id)
    }
    .getOrElse{
      insertPositionQuery(v, user_id, type_id)
    }.transactionally)
    Future{1l}
  }

  override def list(user_id: Long):Future[Seq[A]]              = db.run(listPositionQuery(user_id))
  override def delete(id: Long, user_id: Long)                 = db.run(deletePositionQuery(id, user_id).transactionally)
  def detailByPosition(id: Long, user_id: Long, type_id: Long) = db.run(detailByPositionQuery(id, user_id))
  def move(id: Long, delta: Int, user_id: Long, type_id: Long) = db.run(moveQuery(id, delta, user_id).transactionally)
}