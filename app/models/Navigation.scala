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

case class OptionSet(id: Long, name: String, label: Option[String] = None, assigned: Boolean = false)
case class Link(id: Long, label: String, url: String, sub_id: Option[Long] = None)

object Navigation{
  import controllers._

  lazy val public:List[Link] = List(
    Link(1, "homepage", routes.PublicCtrl.index.url+"#home")
  )

  lazy val client:List[Link] = List()

  lazy val admin:List[Link] = List(
    Link(102, "user",    routes.UserCtrl.index.url),
    Link(107, "company", routes.CompanyCtrl.index.url),
	Link(104, "applications", routes.ApplyCtrl.index.url)
  )
}

object ListFromConf{
  import play.api._

  def string(name: String, default: List[(Int, String)] = Nil):List[OptionSet] = Play.current.configuration.getString(name).flatMap{d =>
    {d.split(",") match{
      case a:Array[String] => Some(a.toList.zipWithIndex.map{case (b,i) => (i+1,b)})
      case _               => None
    }}
  }
  .getOrElse(default)
  .map{case(i,x) => OptionSet(i, x)}

  def list(name: String, default: List[(Int, String)] = Nil):Seq[OptionSet] = Play.current.configuration.getStringList(name)
    .map{d =>
      import scala.collection.JavaConversions._
      d.zipWithIndex.map{case (v,i) =>
        (i,v)
      }
    }
    .getOrElse(default)
    .map{case(i,x) => OptionSet(i, x)}
}

object Status{
  lazy val list:Seq[OptionSet] = ListFromConf.list("status", List((1,"wait")))
}

object Lang{
  lazy val list:Seq[OptionSet] = ListFromConf.list("play.i18n.langs ", List((1, utilities.Constants.lang)))
}