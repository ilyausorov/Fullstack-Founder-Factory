package database

import user_management.user.database._

import slick.driver.MySQLDriver.api._
import slick.lifted.{TableQuery, Tag}
import slick.model.ForeignKeyAction
import org.joda.time.DateTime
import user_management.user.database.JodaToSqlMapper._

class UserExtendedDB(tag: Tag) extends IndexedTableUser[UserExtendedRow](tag, "user_extended"){
  def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id     = column[Long]("user_id")
  def firstName   = column[String]("first_name", O.SqlType("varchar(255)"))
  def lastName    = column[String]("last_name", O.SqlType("varchar(255)"))
  def phone       = column[Option[String]]("phone")

  def user_fk     = foreignKey("position_user_fk", user_id, TableQuery[user_management.user.UserDB])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  def * = (id.? , user_id, firstName, lastName, phone) <> ((UserExtendedRow.apply _).tupled, UserExtendedRow.unapply _)
}

case class UserExtendedRow (
  id:         Option[Long],
  user_id:    Long,
  firstName:  String,
  lastName:   String,
  phone:      Option[String] = None
) extends UniqueUser