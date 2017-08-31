package controllers

import play.api._
import play.api.mvc._
import play.api.routing.{JavaScriptReverseRoute, JavaScriptReverseRouter}

import play.twirl.api.Html

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._ 

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import user_management.user.authentication.AuthenticationService
import user_management.user.models.{Login, Permission}
import play.api.i18n.{Lang, MessagesApi, Messages, I18nSupport}

import play.api.libs.json._
import models._

class Application @Inject()(onStart: models.OnStart) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
	
  def index = Action {implicit request =>
    Redirect(routes.PublicCtrl.index)
  }

  implicit val myJsonFormat = Json.format[models.OptionSet]

  def lang = Action{implicit request =>
    Ok(Json.toJson(models.Lang.list))
  }

  def status = Action{implicit request =>
    Ok(Json.toJson(models.Status.list))
  }

  def jsRoutes(page: String = "default") = Action { implicit request =>

    lazy val router = (jsRoutes:Seq[JavaScriptReverseRoute]) => JavaScriptReverseRouter("jsRoutes")(jsRoutes: _*)

    lazy val basicRoutes = Seq(
		routes.javascript.Application.index,
		routes.javascript.Application.lang,
		routes.javascript.Application.status,

		routes.javascript.LoginCtrl.index,
		routes.javascript.LoginCtrl.login,
		routes.javascript.LoginCtrl.logout,
		routes.javascript.LoginCtrl.askEmailConfirm,

		routes.javascript.AdminCtrl.index,
		
		routes.javascript.PublicCtrl.modalTemplate,
		routes.javascript.PublicCtrl.promptTemplate,
		routes.javascript.PublicCtrl.alertTemplate,
		routes.javascript.PublicCtrl.apply,
		routes.javascript.PublicCtrl.index,

		routes.javascript.UserExtendedCtrl.listAdmin,
		routes.javascript.UserExtendedCtrl.detailAdmin,
		routes.javascript.UserExtendedCtrl.detailCompanyAdmin,
		routes.javascript.UserExtendedCtrl.detail,
		routes.javascript.UserExtendedCtrl.insertAdmin,
		routes.javascript.UserExtendedCtrl.updateAdmin,
		routes.javascript.UserExtendedCtrl.deleteAdmin,
		routes.javascript.UserExtendedCtrl.update,
		routes.javascript.UserExtendedCtrl.updatepAdmin,
		routes.javascript.UserExtendedCtrl.updatep,
		routes.javascript.UserExtendedCtrl.updateEmployer,
		
		routes.javascript.CompanyCtrl.index,
		routes.javascript.CompanyCtrl.listAdmin,
		routes.javascript.CompanyCtrl.detailAdmin,
		routes.javascript.CompanyCtrl.insertAdmin,
		routes.javascript.CompanyCtrl.updateAdmin,
		routes.javascript.CompanyCtrl.deleteAdmin,

		routes.javascript.UserCompanyCtrl.listing,
		routes.javascript.UserCompanyCtrl.detail,	
		routes.javascript.UserCompanyCtrl.update,
		routes.javascript.UserCompanyCtrl.delete,

		routes.javascript.UserFileCtrl.listing,
		routes.javascript.UserFileCtrl.delete,
		routes.javascript.UserFileCtrl.serveLogin,
		
		routes.javascript.UserFileCtrl.deleteLogin,

		routes.javascript.UserCtrl.detail,
		routes.javascript.UserCtrl.findRestrictedUser,
		routes.javascript.UserCtrl.detailCandidate,	
		routes.javascript.UserCtrl.listAdmin,
		
		routes.javascript.ApplyCtrl.index,
		routes.javascript.ApplyCtrl.application,
		routes.javascript.ApplyCtrl.question_types,
		routes.javascript.ApplyCtrl.application_headers,
		routes.javascript.ApplyCtrl.question_list,
		routes.javascript.ApplyCtrl.update,
		routes.javascript.ApplyCtrl.find_application_id,
		routes.javascript.ApplyCtrl.thankyouemail,
		routes.javascript.ApplyCtrl.listAdmin,
		routes.javascript.ApplyCtrl.deleteAdmin,
		routes.javascript.ApplyCtrl.adminViewApplication,
		routes.javascript.ApplyCtrl.detailApplication,
		routes.javascript.ApplyCtrl.updateRead,
		routes.javascript.ApplyCtrl.listAnswers
)

    lazy val adminRoutes = Seq(
		routes.javascript.UserCtrl.detailAdmin,
		routes.javascript.UserCtrl.deleteAdmin,

		routes.javascript.Assets.at
    )

    lazy val userRoutes = Seq(
		user_management.user.controllers.routes.javascript.PermissionCtrl.listWithAssign,
		user_management.user.controllers.routes.javascript.PermissionCtrl.assign,
		user_management.user.controllers.routes.javascript.PermissionCtrl.revoke,
		user_management.user.controllers.routes.javascript.UserManagementCtrl.add,
		user_management.user.controllers.routes.javascript.UserManagementCtrl.addWithCompany,
		
		routes.javascript.LoginCtrl.changeUserName,
		routes.javascript.LoginCtrl.loginPostAction,
		routes.javascript.LoginCtrl.askPasswordReset,
		
		user_management.user.controllers.routes.javascript.UserManagementCtrl.changeStatus,
		user_management.user.controllers.routes.javascript.UserManagementCtrl.changePassword,
		user_management.user.controllers.routes.javascript.UserManagementCtrl.resetPassword,
		user_management.user.controllers.routes.javascript.UserManagementCtrl.reinitPassword,
		user_management.user.controllers.routes.javascript.UserManagementCtrl.resetWebServicePassword,
		user_management.user.controllers.routes.javascript.UserManagementCtrl.delete,
		
		controllers.routes.javascript.UserCtrl.insertAdmin,
		
		user_management.user.controllers.routes.javascript.UserManagementCtrl.updateAdmin,
		user_management.user.controllers.routes.javascript.UserManagementCtrl.deleteAdmin,
		user_management.user.controllers.routes.javascript.UserManagementCtrl.changePasswordAdmin,
		user_management.user.controllers.routes.javascript.UserManagementCtrl.changeWebservicePasswordAdmin,
		user_management.user.controllers.routes.javascript.UserManagementCtrl.findRestrictedUserByKey,
		
		controllers.routes.javascript.UserCtrl.generatePasswordAdmin
    )

    Ok(
      page match{
        case _ => router(basicRoutes++adminRoutes++userRoutes)
      }
    ).as("text/javascript")
  }
}