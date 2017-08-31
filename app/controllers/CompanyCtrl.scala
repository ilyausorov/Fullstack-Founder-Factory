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
import user_management.user.models.{Login, Permission}
import play.api.i18n.{Lang, MessagesApi, Messages, I18nSupport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import user_management.user.models.{Permissions, UserRow, Credentials, UserSocial, UserThread, UserPhone, UserEmail, UserCompany, UserCompanyRow}
import user_management.user.models.{Company, CompanyRow}

class CompanyCtrl @Inject()(userService: user_management.user.UserManagementService, helpers: user_management.play.utils.controller.FormHelper, actions: Actions) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
  import actions._
  implicit val myJsonFormat = Json.format[CompanyRow]

  def index = (IsAuthenticated andThen IsAdmin){implicit request =>
    Ok(views.html.admin_dashboard.company.index())
  }

  def edit = (IsAuthenticated andThen IsAdmin){implicit request =>
    helpers.single.bindFromRequest.fold(
      e => Redirect(routes.CompanyCtrl.add),
      v => Redirect(routes.CompanyCtrl.editM(v))
    )
  }

  def editM(id: Long) = editT(Some(id))

  def add = editT()

  def editT(id: Option[Long] = None) = (IsAuthenticated andThen IsAdmin){implicit request =>
    Ok(views.html.admin_dashboard.company.edit(id))
  }

  /* SERVICE */
  def listAdmin = IsAuthenticated.async{implicit request =>
    Company.list.map{c =>
      Ok(Json.toJson(c))
    }
  }

  def detailAdmin(id: Long) = IsAuthenticated.async{implicit request =>
    Company.detail(id).map{u=>
      Ok(Json.toJson(u))
    }
  }

  def insertAdmin = IsAuthenticated.async{implicit request=>
   myForm.bindFromRequest.fold(
      e => helpers.error[CompanyRow](e),
      v => Company.update(v).flatMap{i => helpers.id(i)}
    )
  }

  def updateAdmin(id: Long) = IsAuthenticated.async{implicit request =>
    myForm.bindFromRequest.fold(
      e => helpers.error[CompanyRow](e),
      v => Company.update(v).flatMap{i => helpers.id(i)}
    )
  }

  def deleteAdmin(id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    Company.delete(id).flatMap{i => helpers.id(i)}
  }

  val myForm = Form(user_management.user.Mappings.company)
}