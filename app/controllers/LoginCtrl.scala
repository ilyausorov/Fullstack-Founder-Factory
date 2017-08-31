package controllers

import javax.inject.Inject

import user_management.user.actions.UserRequest
import user_management.user.models.{RestrictedUser, Constants, Credentials}

import play.api.cache.CacheApi
import play.api.mvc._

import user_management.play.utils.controller.Helper
import user_management.play.utils.controller.FormHelper
import user_management.user.Forms
import user_management.user.authentication.AuthenticationService
import play.api.i18n.{Messages, MessagesApi, I18nSupport}
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.i18n.MessagesApi

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._ 

import play.api.libs.json._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class NewUser(
  username2: String,
  password2: String = utilities.encryption.Random.generate(8),
  permissions: Seq[Long] = Seq(5l),
  company: Option[String] = None,
  role: Option[String] = None,
  status_id: Long = 3
) extends _root_.database.user.models.NewUser{

  val password = user_management.user.Password.hash(password2)
  val username = username2

  import utilities.encryption._

  val md5pass  = Md5.hash(Random.generate(12))
  val key      = Random.generate(12) 

  def toRow = user_management.user.models.UserRow(
    None
    , 0
    , username
    , password
    , md5pass
    , key
    , status_id
    , "en"
  )
} 

class LoginCtrl @Inject()(
  userService: user_management.user.UserManagementService,
  userExtendedService: models.UserExtended,
  authenticationService: AuthenticationService,
  helper: Helper,
  formHelper: FormHelper,
  actions:Actions) (val messagesApi: MessagesApi) extends Controller with I18nSupport {
  import actions._

  lazy val onSuccess  = routes.LoginCtrl.loginPostAction

  def redirect(url: String) = Action{
    Redirect(url)
  }

  def loginPostAction = IsAuthenticated{implicit request =>
    // redirects user depending on `permissions`
    val url = request.user.permissions match{
      case a if a.contains(1) => routes.AdminCtrl.index
      case a if a.contains(5) => routes.PublicCtrl.index
      case a if a.contains(7) => routes.PublicCtrl.index
      case _                  => routes.PublicCtrl.index
    }

    Ok(JsObject(Seq("url" -> JsString(url.url))))
  }

  lazy val onUnauthorized = BadRequest(Json.obj("" -> Messages("error.login.wrongCredentials"), "username" -> Messages("error.login.wrongCredentials"), "password" -> Messages("error.login.wrongCredentials")))

  def indexAdmin = Action{implicit request =>
    Ok(views.html.login_components.index())
  }

  def index = Action{
    Redirect(routes.PublicCtrl.index)
  }

  def login = Action.async{implicit request =>

    val redirectUrl = Form(single("redirect" -> nonEmptyText)).bindFromRequest.fold(
      e => onSuccess,
      v => routes.LoginCtrl.redirect(v)
    )

    Forms.login.bindFromRequest.fold(
      e => formHelper.error(e),
      v => authenticationService.login(v, redirectUrl, onUnauthorized)
    )
  }

  def logout = IsAuthenticated{implicit request =>
      authenticationService.logout(Redirect(routes.Application.index()))
  }

  def signup = Action.async{implicit request =>
    signupForm.bindFromRequest.fold(
      e => formHelper.error(e),
      v => {
        val company = user_management.user.models.CompanyRow(None, v._1._4)
        val user    = (new NewUser(v._1._3, v._2._1))
        val userExt = database.UserExtendedRow(None, 0, v._1._1, v._1._2, Some(v._1._5))
        userExtendedService.insert(user, userExt, company).flatMap{id =>
          val c = user_management.user.models.Login(v._1._3, v._2._1)
		  userExtendedService.askEmailChange(v._1._3)
          authenticationService.login(c,onSuccess,onUnauthorized)
        }
      }
    )
  }

  def changeUserName = IsAuthenticated.async{implicit request =>
    Form(emailMapping).bindFromRequest.fold(
      e => Future(BadRequest(e.errorsAsJson)),
      v => userService.changeUsername(request.user.id.get,v._2).flatMap{fu => 
        if(fu){ 
			userService.find(v._2).map{userr=>
				userService.emailChange(userr.get.id.get, userr.get.key).map{changed=>
					userExtendedService.askEmailChange(v._2)		
					}
				}
				helper.ok()
			}
        else{Future(BadRequest(JsObject(Seq("error"  -> JsString(Messages("error.username.taken"))))))}
      }
    )
  }

 def askEmailConfirm = IsAuthenticated.async{implicit request =>
	  userService.find(request.user.username).flatMap{userr=>
		if(userr.get.status_id == 3){
			userExtendedService.askEmailChange(request.user.username).flatMap{xyz=>
					helper.ok()
				}
			}
		else{
				helper.error()
			}
		  }
		}

  def emailConfirm(id: Long, key: String) = IsAuthenticated.async{implicit request =>
		userService.activate(id, key).flatMap{changed=>
			if(changed){ 
				userService.findRestrictedUserByKey(id, key).map{userr=>
					userService.resetKey(id)
					Ok("Email successfully confirmed. You can now close this window.")
				}
			}
			else{ Future.successful(Ok("Unable to confirm your email address. Please request a new email address confirm email.") )}
		}
	}
	
	
  def askPasswordReset = Action.async{implicit request =>
   usernameForm.bindFromRequest.fold(
      e => Future(Ok(e.errorsAsJson)),
      v => userExtendedService.askPasswordReset(v.username).flatMap{w =>
        helper.ok()
      }
    )
  }

  def passwordResetForm(id: Long, keyy: String) = Action{implicit request =>
     Ok(views.html.login_components.resetpassword.index(id, keyy))                                                      
  }

  def profile = IsAuthenticated{implicit request =>
    Ok("ok")
  }

  val emailMapping = tuple(
    "id"    -> optional(longNumber),
    "email" -> email
  )
  .verifying("error.email.taken", result => result match{case (id, e) => 
    import scala.concurrent._
    import scala.concurrent.duration._
    !Await.result(userService.exists(e, id), 5000 millis) 
  })

  val myMapping = tuple(
    "firstName"     -> nonEmptyText,
    "lastName"      -> nonEmptyText,
    "email"         -> email,
    "company"       -> nonEmptyText,
    "phone"         -> nonEmptyText,
    "role"          -> optional(text)
  )
  .verifying("error.email.taken", result => result match{case (f,l,e,c,p,r) => 
    import scala.concurrent._
    import scala.concurrent.duration._
    !Await.result(userService.exists(e), 5000 millis) 
  })
  
  val signupForm = Form(
    tuple(
      "login"     -> myMapping,
      "passwords" -> user_management.user.Mappings.newPassword
    )
  )

case class loginform(username: String, password: Option[String])

  val usernameL = mapping(
	"username" -> email,
	"password" -> optional(nonEmptyText)
)(loginform.apply)(loginform.unapply)

  val usernameForm = Form(usernameL)
}