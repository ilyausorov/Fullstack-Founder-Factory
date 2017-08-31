package controllers

import play.api._
import play.api.mvc._
import play.api.routing.{JavaScriptReverseRoute, JavaScriptReverseRouter}

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

class PublicCtrl @Inject()(
  
)(val messagesApi: MessagesApi) extends Controller with I18nSupport{


  def index = Action{implicit request =>
    Ok(views.html.landing_page.index())
  }

  def emaillogin = Action{implicit request  =>
    Ok(views.html.login_components.emaillogin())
  }	

  def privacy = Action{implicit request =>
  	Ok(views.html.privacy_page.index()) 
  }	
	
  def terms = Action{implicit request =>
  	Ok(views.html.terms_page.index()) 
  }
	
  def apply = Action{implicit request =>
  	Ok(views.html.apply_page.index()) 
  }	
	
  def fourohfour = Action{implicit request =>
    Ok(views.html.error_page.index("g"))
  }  

  def faqHtml = Action{implicit request =>
    Ok(views.html.faq_page.index())
  }
  
  def modalTemplate = Action{implicit request =>
	Ok(views.html.common_components.angular_components.modal())
  }

  def alertTemplate = Action{implicit request =>
	Ok(views.html.common_components.angular_components.alert())
  }

  def promptTemplate = Action{implicit request =>
	Ok(views.html.common_components.angular_components.prompt())
  }

}
