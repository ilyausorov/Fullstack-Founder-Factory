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

import database._
import models._
import user_management.user.models._

class CalendarCtrl @Inject()(
  userService: user_management.user.UserManagementService,
  userExtendedService: UserExtended,
  userCompanyService: user_management.user.models.UserCompany,
  applyService: Apply,
  loginCtrl: LoginCtrl,
  helpers: user_management.play.utils.controller.Helper,
  actions: Actions
) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
  import actions._

	def index = IsAuthenticated{implicit request=>
		Ok(views.html.calendar_page.index())
	}
 
}