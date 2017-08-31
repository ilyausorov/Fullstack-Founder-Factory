package database

import user_management.user.database._

import slick.driver.MySQLDriver.api._
import slick.lifted.{TableQuery, Tag}
import slick.model.ForeignKeyAction
import user_management.user.database.JodaToSqlMapper._
import com.github.nscala_time.time.Imports._
import org.joda.time.DateTime

class NewsletterDB(tag: Tag) extends IndexedTable[models.Newsletter](tag, "newsletter"){
  def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def email       = column[String]("email", O.SqlType("varchar(254)"))
  def key         = column[String]("keyy", O.SqlType("varchar(254)"))
  def status_id   = column[Int]("status")
  def date_added  = column[DateTime]("date_added")

  def * = (id.? , email, key, status_id, date_added) <> ((models.Newsletter.apply _).tupled, models.Newsletter.unapply _)
}
