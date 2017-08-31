package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._

import play.api.Play.current

import play.api.libs.json.Json
import play.api.libs.json.JsValue

import javax.inject.Inject

import user_management.user.authentication.AuthenticationService
import user_management.user.models._
import play.api.i18n.{Lang, MessagesApi, Messages, I18nSupport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import user_management.user.models.{Permissions, UserRow, Credentials, UserSocial, UserThread, UserPhone, UserEmail, UserCompany, UserCompanyRow}

class UserCompanyCtrl @Inject()(
  userCompanyService: UserCompany,
  helpers: user_management.play.utils.controller.FormHelper,
  actions: Actions
) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
  import actions._
  implicit val myJsonFormat = Json.format[UserCompanyRow]

  def listing(id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    userCompanyService.listByCompany(id).map{u=>
      Ok(Json.toJson(u))
    }
  }

 def detail = IsAuthenticated.async{implicit request =>
    userCompanyService.list(request.user.id.get).map{s =>
      Ok(Json.toJson(s))
    }
  }

	
  def update = IsAuthenticated.async{implicit request =>
    myForm.bindFromRequest.fold(
      e => helpers.error[UserCompanyRow](e),
      v => userCompanyService.update(v).flatMap{id => helpers.id(id)}
    )
  }
 

  def delete = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    helpers.double.bindFromRequest.fold(
      e => helpers.error[(Long, Long)](e),
      v => {
        userCompanyService.delete(v._1, v._2)
        helpers.ok()
      }
    )
  }

  val myForm = Form(
    mapping(
      "id"          -> optional(longNumber)
      , "user_id"   -> longNumber
      , "company_id"   -> default(longNumber, 0l)
      , "role"     -> optional(text)
    )
    (UserCompanyRow.apply)(UserCompanyRow.unapply)
  )
}