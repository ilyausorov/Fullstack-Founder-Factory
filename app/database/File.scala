package database

import user_management.user.database._

import slick.driver.MySQLDriver.api._
import slick.lifted.{TableQuery, Tag}
import slick.model.ForeignKeyAction
import org.joda.time.DateTime
import com.github.tototoshi.slick.MySQLJodaSupport._

case class UserPicRow(
  id: Option[Long]
  , user_id: Long
  , key: String
  , name: String
  , contentType: Option[String] = None
  , type_id: Long = 1l
  , date: org.joda.time.DateTime = new org.joda.time.DateTime
) extends UniqueUser

class UserPicDB(tag: Tag) extends IndexedTableUser[database.UserPicRow](tag, "user_pic"){
  def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id     = column[Long]("user_id")
  def key         = column[String]("keyy",O.SqlType("varchar(254)"))
  def name        = column[String]("name",O.SqlType("varchar(254)"))
  def contentType = column[Option[String]]("content_type",O.SqlType("varchar(254)"))
  def type_id     = column[Long]("type_id")
  def date_added  = column[DateTime]("date_added",O.SqlType("DATETIME"))

  def user_fk     = foreignKey("userpic_user_fk", user_id, TableQuery[user_management.user.UserDB])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  def * = (id.? , user_id, key, name, contentType, type_id, date_added) <> ((UserPicRow.apply _).tupled, UserPicRow.unapply _) 
}

case class UserFileRow(
  id: Option[Long]
  , user_id: Long
  , key: String
  , name: String
  , contentType: Option[String] = None
  , type_id: Long = 1l
  , date: org.joda.time.DateTime = new org.joda.time.DateTime
) extends UniqueUser

class UserFileDB(tag: Tag) extends IndexedTableUser[database.UserFileRow](tag, "user_file"){
  def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id     = column[Long]("user_id")
  def key         = column[String]("keyy",O.SqlType("varchar(254)"))
  def name        = column[String]("name",O.SqlType("varchar(254)"))
  def contentType = column[Option[String]]("content_type",O.SqlType("varchar(254)"))
  def type_id     = column[Long]("type_id")
  def date_added  = column[DateTime]("date_added",O.SqlType("DATETIME"))

  def user_fk     = foreignKey("userfile_user_fk", user_id, TableQuery[user_management.user.UserDB])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  def * = (id.? , user_id, key, name, contentType, type_id, date_added) <> ((UserFileRow.apply _).tupled, UserFileRow.unapply _) 
}