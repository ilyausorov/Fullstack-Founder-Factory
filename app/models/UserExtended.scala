package models

import user_management.user.database.{CRUDByUser11, CRUDByUser}
import database.{UserExtendedDB, UserExtendedRow}
import play.api.Play
import play.api.Play.current
import play.api.mvc.Request
import slick.driver.MySQLDriver
import slick.lifted.TableQuery
import org.joda.time.DateTime
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.MySQLDriver.api._

import user_management.user.models._

case class UserFull(
  id: Long,
  email: String,
  user: UserExtendedRow,
  company: String,
  role: Option[String],
  img_url: Option[String] = None,
  status_id: Long,
  key: String
)

class UserExtended @Inject()(
  userService: user_management.user.UserManagementService,
  userCompanyService: user_management.user.models.UserCompany
) extends CRUDByUser11[UserExtendedDB, UserExtendedRow] {
  override val query = TableQuery[UserExtendedDB]

  def askPasswordReset(email: String):Future[Boolean]={
    userService.findRestrictedUser(email).flatMap{fe=>
      fe.map{user=>
		detail(user.id.get).map{moreinfo=>
			val ce = views.html.email_templates.passwordreset(user.id.get, user.key, user.username,moreinfo.get.user.firstName+" "+moreinfo.get.user.lastName).body
			val e = new utilities.Email(Seq(email),  "Password Reset", ce, true)
			e.setFrom(utilities.Constants.name+" "+"<"+utilities.Constants.email+">")
			e.send
			true
		}
      }.getOrElse(Future.successful(false))
    }
  }

def askEmailChange(email: String):Future[Boolean]={
    userService.findRestrictedUser(email).flatMap{fe=>
      fe.map{user=>
		detail(user.id.get).map{moreinfo=>
			val ce = views.html.email_templates .emailchange(user.id.get, user.key, user.username,moreinfo.get.user.firstName+" "+moreinfo.get.user.lastName).body
			val e = new utilities.Email(Seq(email),  "Confirm your email address on "+utilities.Constants.name, ce, true)
			e.setFrom(utilities.Constants.name+" "+"<"+utilities.Constants.email+">")
			e.send
			true
		}
      }.getOrElse(Future.successful(false))
    }
  }

  def listAll = db.run{
    query.result
  }

  def listAllFull:Future[Seq[(UserRow, Option[UserExtendedRow])]] = userService.list.flatMap{fu =>
    listAll.map{fc=>
      fu.map{u=>
        (
          u,
          fc.filter(_.user_id == u.id.get).headOption
        )
      }
    }
  }

  def listPublic:Future[Seq[UserFull]] = {
    val fResults = for{
      x <- userService.list
      y <- listAll
      z <- db.run{userCompanyService.query.result}
      c <- Company.list
    } yield(x,y,z,c)
    fResults.map{case (users, usersExtended, usersCompany, companys) =>
      users.flatMap{user =>
        usersExtended.filter(_.user_id == user.id.get).headOption.flatMap{userExtended =>
          usersCompany.filter(_.user_id == user.id.get).headOption.flatMap{userCompany =>
            companys.filter(_.id == Some(user.company_id)).headOption.map{company =>
              UserFull(user.id.get, user.username, userExtended, company.name, userCompany.role, None, user.status_id, user.key)
        }
      }
    }
  }
 }
}
  def detail(id: Long):Future[Option[UserFull]] = {
    val fResults = for{
      x <- userService.detail(id)
      y <- listAll
      z <- db.run{userCompanyService.query.result}
      c <- Company.list
      i <- models.UserPic.last(id)
    } yield(x,y,z,c,i)

    fResults.map{case (user, usersExtended, usersCompany, companys, image) =>
      val img = image.map{userPic =>
        controllers.routes.UserPicCtrl.serve(userPic.id.get, id).url
      }
      user.flatMap{user =>
        usersExtended.filter(_.user_id == user.id.get).headOption.flatMap{userExtended =>
          usersCompany.filter(_.user_id == user.id.get).headOption.flatMap{userCompany =>
            companys.filter(_.id == Some(user.company_id)).headOption.map{company =>
              UserFull(user.id.get, user.username, userExtended, company.name, userCompany.role, img, user.status_id, user.key)
            }
          }
        }
      }
    }
  }
				 
  def insert(
    userRow: controllers.NewUser,
    userExtendedRow: UserExtendedRow,
    companyRow: CompanyRow,
    permissions: Seq[Long] = Seq(2),
    role: Option[String] = None
  ):Future[Long] = userService.add(companyRow, userRow, role).flatMap{user =>
    user.id.map{id =>
    update(userExtendedRow.copy(user_id = id), id).map{f=>
		id
      }
    }.getOrElse(Future(0l))
  }
}