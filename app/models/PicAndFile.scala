package models

import user_management.user.database.{UniqueUser, CRUDByUser, CRUDByUser11}

import org.joda.time.DateTime

import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._

import scala.language.postfixOps

object UserPic extends CRUDByUser11[database.UserPicDB, database.UserPicRow]{

  val query = TableQuery[database.UserPicDB]
  def filename(id: Long) = utilities.file.File.name(id, tableName)

  def last(user_id: Long, type_id: Long = 1):Future[Option[database.UserPicRow]] = list(user_id).map{f =>
    f.sortBy(_.id).filter(_.type_id == 1).headOption
  }

  def lastWait(user_id: Long, type_id: Long = 1):Option[database.UserPicRow] = try{
      import scala.concurrent.duration._
      Await.result(last(user_id, type_id), 15 seconds)
    }
    catch{
      case _:Throwable => None
    }

  def lastId(company_id: Long, type_id: Long=1):Option[Long] = lastWait(company_id, type_id).flatMap{_.id}
}


object UserFile extends CRUDByUser[database.UserFileDB, database.UserFileRow]{
  val query = TableQuery[database.UserFileDB]
  def filename(id: Long) = utilities.file.File.name(id, tableName)

  def last(user_id: Long, type_id: Long = 1):Future[Option[database.UserFileRow]] = list(user_id).map{f =>
    f.sortBy(_.id).filter(_.type_id == 1).headOption
  }

  def lastWait(user_id: Long, type_id: Long = 1):Option[database.UserFileRow] = try{
      import scala.concurrent.duration._
      Await.result(last(user_id, type_id), 15 seconds)
    }
    catch{
      case _:Throwable => None
    }
}