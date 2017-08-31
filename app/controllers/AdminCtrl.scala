package controllers

import javax.inject.Inject

import user_management.play.utils.controller.Helpers
import user_management.user.Forms
import user_management.user.authentication.AuthenticationService
import play.api.i18n.{Messages, MessagesApi, I18nSupport}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import javax.inject.Inject

import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.i18n.MessagesApi


class AdminCtrl @Inject()(authenticationService: AuthenticationService, helpers: Helpers, actions:Actions) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
  import actions._

  def index = (IsAuthenticated andThen IsAdmin){implicit request =>
    Ok(views.html.admin_dashboard.site_components.index())
  }
	
}