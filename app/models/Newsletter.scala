package models

import play.api.Play.current
import org.joda.time.DateTime

import user_management.user.actions.UserRequest
import play.api.mvc.AnyContent
import user_management.user.database._

import slick.driver.MySQLDriver
import slick.lifted.TableQuery

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api._

case class Newsletter(id: Option[Long], email: String, key: String = utilities.encryption.Random.alphanumeric(12), status_id: Int = 0, date_added: DateTime = new DateTime()) extends Unique

object Newsletter extends CRUD[database.NewsletterDB, Newsletter]{
  val query = TableQuery[database.NewsletterDB]

  def listSelect:Future[Seq[(String, String)]] = list.map{fa => fa.map{a => (a.id.get.toString, a.email)}}

  def changeStatus(id: Long, key: String, status: Int = 1) = list.map{fl =>
    fl.filter(x => x.id.get == id && x.key == key).headOption.map{x =>
      update(x.copy(status_id = status))
    }
  }
}