
package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._

import play.api.Play.current

import play.api.libs.json._

import javax.inject.Inject

import user_management.user.authentication.AuthenticationService
import user_management.user.models.{Login, Permission}
import play.api.i18n.{Lang, MessagesApi, Messages, I18nSupport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import user_management.user.models.UserRow
import models._
import database._
  
class UserExtendedCtrl @Inject()(
	userExtended: UserExtended, 
	helpers: user_management.play.utils.controller.Helper, 
	actions: Actions
) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
  import actions._
  

  /* SERVICE */
  implicit val myJsonFormat0 = Json.format[OptionSet]
  implicit val myJsonFormat1 = Json.format[UserExtendedRow]
  implicit val myJsonFormat2 = Json.format[UserRow]
  implicit val myJsonFormat4 = Json.format[UserFull]

  /**
   * returns all candidate with user entity
   */
  def listAdmin = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    userExtended.listAllFull.map{c =>
      Ok(JsArray(c.map{case (u, e) =>
        e.map{f=>
          Json.toJson(f).as[JsObject]++Json.toJson(u).as[JsObject]
        }.getOrElse{
          Json.toJson(u)
        }
        
      }))
    }
  }

  def detailAdmin(id: Long) = IsAuthenticated.async{implicit request =>
    userExtended.listSingle(id).map{l =>
      Ok(Json.toJson(l))
    }
  }
	
	 def detailCompanyAdmin(id: Long) = IsAuthenticated.async{implicit request =>
    userExtended.detail(id).map{l =>									
      Ok(Json.toJson(l))
    }
  }

  def updateInsertAdmin(id: Option[Long] = None) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    import play.api.libs.json._
    
    myForm.bindFromRequest.fold(
      e => helpers.error[UserExtendedRow](e),
       v => userExtended.update(v.copy(id = id), v.user_id).flatMap{i => 
        id.map{iid =>
          helpers.id(i)
        }.getOrElse(
          helpers.id(i, 201)
        )
      } 
    )
  }

  def insertAdmin           = updateInsertAdmin()

  def updateAdmin(id: Long) = updateInsertAdmin(Some(id))

  def deleteAdmin(id: Long) = IsAuthenticated.async{implicit request =>
    userExtended.delete(id).flatMap{i => helpers.id(i)}
  }

  def detail = IsAuthenticated.async{implicit request =>
    userExtended.listSingle(request.user.id.get).map{l =>
      Ok(Json.toJson(l))
    }
  }
	
  def updatep = IsAuthenticated.async{implicit request =>			  
    myForm.bindFromRequest.fold(
      e => Future(BadRequest(e.errorsAsJson)),
       x => userExtended.update(x.copy(user_id = request.user.id.get)).flatMap{i =>
            
			helpers.id(i)
        }
	   )
      }

	
  def update = IsAuthenticated.async{implicit request =>
    myForm.bindFromRequest.fold(
      e => Future(BadRequest(e.errorsAsJson)),
      x => x match{
        case (v) => userExtended.update(v.copy(user_id = request.user.id.get)).flatMap{i =>
            helpers.id(i)
        } 
      }
    )
  }
	
	  def updatepAdmin(ueid: Long, ceid: Long, user_id: Long) = IsAuthenticated.async{implicit request =>
    myForm.bindFromRequest.fold(
      e => Future(BadRequest(e.errorsAsJson)),
      x => x match{
        case (v) => userExtended.update(v.copy(user_id = user_id), user_id).flatMap{i =>
            helpers.id(i)          
        } 
      }
    )
  }


	  def updateEmployer = IsAuthenticated.async{implicit request =>
    myForm.bindFromRequest.fold(
      e => Future(BadRequest(e.errorsAsJson)),
      x => userExtended.update(x.copy(user_id = request.user.id.get)).flatMap{i => helpers.id(i)
        } 
      )
    }
 
	
  val myMapping = mapping(
      "id"         -> optional(longNumber),
      "user_id"    -> longNumber,
      "firstName"  -> nonEmptyText,
      "lastName"   -> nonEmptyText,
      "phone"      -> optional(text)
    )(UserExtendedRow.apply)(UserExtendedRow.unapply)

  val myForm = Form(myMapping)

}