package user_management.user.controllers

import javax.inject.Inject

import user_management.play.utils.controller.Helpers
import user_management.user.actions.{UserRequest, StandardActions}
import user_management.user.models.Company
import user_management.user.{Mappings, UserManagementService, Forms}
import play.api.data.Form
import play.api.i18n.{Messages, MessagesApi, I18nSupport}
import play.api.libs.json._
import play.api.mvc.Controller
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Implementation of a User Management Controller
 * Please make sure to never expose the user_id or hashes to the outside world.
 * @param userService
 * @param helpers
 * @param actions
 */
class UserManagementCtrl @Inject()(
  userService: UserManagementService,
  helpers:Helpers,
  actions:StandardActions) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
  import actions._

  lazy val success         = Json.obj("status" -> "success")
  lazy val userNameTaken   = Json.obj("error"  -> "username.taken",          "message" -> Messages("error.username.taken"))
  lazy val passwordNoMatch = Json.obj("error"  -> "password.did.not.match",  "message" -> Messages("error.password.did.not.match"))
  lazy val unknownCompany  = Json.obj("error"  -> "unknown.company",         "message" -> Messages("error.unknown.company"))
  lazy val unknownUserid   = Json.obj("error"  -> "unknown.user_id",         "message" -> Messages("error.unknown.user_id"))
  lazy val notConfirmed    = Json.obj("error"  -> "email.not_confirmed",     "message" -> Messages("error.email.not_confirmed"))

  /** ADMIN **/

  def insertAdmin = insertUpdateAdmin()

  def updateAdmin(id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    Form(Mappings.user).bindFromRequest.fold(
      e => Future(BadRequest(e.errorsAsJson)),
      v => userService.update(v)).map{_ => Ok(success)}
  }

  def insertUpdateAdmin(id: Option[Long] = None)= (IsAuthenticated andThen IsAdmin).async{implicit request =>
    Form(Mappings.user).bindFromRequest.fold(
      e => Future(BadRequest(e.errorsAsJson)),
      v => userService.exists(v.username, v.id).flatMap{r =>

        if (r) userService.update(v.copy(id = id)).map{_ => Ok(success)}
        else Future(BadRequest(unknownUserid))

      }
    )
  }

  def changePasswordAdmin(id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    changePassword(id, userService.changePassword(_,_))
  }

  def changeWebservicePasswordAdmin(id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    changePassword(id, userService.changeWebServicePassword(_,_))
  }

  private def changePassword(id: Long, f: (Long, String) => Future[Int])(implicit request:user_management.user.actions.UserRequest[AnyContent]) =
    Forms.passwordAdmin.bindFromRequest.fold(
      e => Future(BadRequest(e.errorsAsJson)),
      v => f(id, v._1).map{status =>
        if (status > 0) Ok(success)
        else            BadRequest(unknownUserid)
      }
  )

  /**
   * re-generate password, sets status to "ok" and regenerate email and sends it to user
   */
  def generatePasswordAdmin(id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    val password = utilities.encryption.Random.generate(8)

    userService.changePassword(id, password).map{status =>
      if (status > 0) Ok("ok")
      else BadRequest
    }

  }

  def deleteAdmin(id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    userService.delete(id).map(_ => Ok(success))
  }

  /** PROFILE */

  /**
   * Change username by user
   * @return
   */
  def changeUserName = IsAuthenticated.async{implicit request =>
    Forms.changeUserName.bindFromRequest.fold(
      e => helpers.errorToJson(e),
      v => userService.exists(v,request.user.id).flatMap{r =>
        if(r) Future(BadRequest(userNameTaken))
        else  userService.changeUsername(request.user.id.get,v).map(_ => Ok(success))
      }
    )
  }

  /**
   * Change password by user
   * @return
   */
  def changePassword = IsAuthenticated.async{implicit request =>
    Forms.changePassword.bindFromRequest.fold(
      e => helpers.errorToJson(e),
      {case (password, newPasswords) =>
        userService.checkPassword(password).flatMap(matched => 
          if (matched) userService.changePassword(request.user.id.get,newPasswords._1).map(_ => Ok(success))
          else         Future.successful(BadRequest(passwordNoMatch))
        )
      }
    )
  }

  def passwordReinit = IsAuthenticated.async{implicit request =>
    Forms.passwordReinit.fold(
      e => Future(BadRequest(e.errorsAsJson)),
      {case (id, key, (new1, new2)) =>
        userService.passwordReinit(id,key, new1).map{status =>
          if (status > 0) Ok(success)
          else            BadRequest(unknownUserid)
        }
      }
    )
  }

  /**
   * Add a new user with existing company
   * @return
   */
  def add = Action.async{implicit request => addUser}

  def addAdmin = (IsAuthenticated andThen IsAdmin).async{implicit request => addUser}

  private def addUser(implicit request:Request[AnyContent]) = {
    Forms.newUser.bindFromRequest.fold(
      e => helpers.errorToJson(e),
      v => userService.find(v.username).flatMap(_.map(user =>
        Future.successful(BadRequest(userNameTaken))).getOrElse(
          Company.detail(v.company_id.getOrElse(0L)).flatMap(_.map(company =>
            userService.add(v).map(user => Ok(Json.obj("id" -> user.id)))
          )
            .getOrElse(Future.successful(BadRequest(unknownCompany))))
        )
      )
    )
  }

  /**
   * Add a new user with corresponding company
   * @return
   */
  def addWithCompany = Action.async{implicit request => addUserWithCompany}

  def addWithCompanyAdmin = (IsAuthenticated andThen IsAdmin).async{implicit request => addUserWithCompany}

  private def addUserWithCompany(implicit request:Request[AnyContent]) = {
    Forms.newUserWithCompany.bindFromRequest.fold(
    e => helpers.errorToJson(e),
    {case (company,user) =>
      userService.find(user.username).flatMap(_.map(_ =>
        Future.successful(BadRequest(userNameTaken)))
        .getOrElse(userService.add(company,user).map(user =>
        Ok(Json.obj("id" -> user.id))
      ))
      )
    }
    )
  }

  def addWithCompanyAndUserExtended = Action.async{implicit request => addUserWithCompanyAndUserExtended}

  def addWithCompanyAndUserExtendedAdmin = (IsAuthenticated andThen IsAdmin).async{implicit request => addUserWithCompanyAndUserExtended}

  private def addUserWithCompanyAndUserExtended(implicit request:Request[AnyContent]) = {
    Forms.newUserWithCompanyAndExtUser.bindFromRequest.fold(
    e => helpers.errorToJson(e),
    {case (company, user, userExtended) =>
      userService.find(user.username).flatMap(_.map(_ =>
        Future.successful(BadRequest(userNameTaken)))
        .getOrElse(userService.add(company,user,userExtended,None).map(user =>
        Ok(Json.obj("id" -> user.id))
      ))
      )
    }
    )
  }


	def findRestrictedUserByKey(id: Long, key: String) = Action.async{implicit request=>
		userService.findRestrictedUserByKey(id, key).map{userid=>
			userid.map{useridx=>
				Ok(Json.obj("email" -> useridx.username))
			}.getOrElse(BadRequest("Failed"))
		}
	}


  /* TODO */



  /**
   * re-generate webservice password, sets status to "ok" and regenerate email and sends it to user
   */
  def generateWebservicePassword(id: Long) = TODO


  /**
   * Delete user via Json
   * @return
   */
  def delete = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    Forms.deleteUser.bindFromRequest.fold(
       e => helpers.errorToJson(e),
       v => userService.delete(v).map(_ => Ok(success))
    )
  }

  /**
   * Change status of single user
   * @return
   */
  def changeStatus = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    Forms.changeStatus.bindFromRequest.fold(
      e => helpers.errorToJson(e),
      {case (id,key,status) =>  userService.changeStatus(id,key,status).map(_ => Ok(success))}
    )
  }

  /**
   * Reset password for single user
   * @return
   */
  def resetPassword = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    Forms.resetPassword.bindFromRequest.fold(
      e => helpers.errorToJson(e),
      {case (user_id, newPasswords) =>
        userService.changePassword(user_id,newPasswords._1).map(status =>
          if (status > 0) Ok(success)
          else            BadRequest(unknownUserid)
        )
      }
    )
  }

  /**
   * Reset webservice password for single user
   * @return
   */
  def resetWebServicePassword = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    Forms.resetPassword.bindFromRequest.fold(
       e => helpers.errorToJson(e),
       {case (user_id, newPasswords) =>
        userService.changeWebServicePassword(user_id,newPasswords._1).map{status => 
          if (status > 0) Ok(success)
          else            BadRequest(unknownUserid)
        }
      }
    )
  }

  /**PUBLIC**/
  /**
   * Re-initialize the password by user-id and key
   * @param id
   * @param key
   * @return
   */
  def reinitPassword(id:Long,key:String) = Action.async{implicit request =>

    Form(Mappings.newPassword).bindFromRequest.fold(
      e => helpers.errorToJson(e),
      {case (password,repeat) => userService.passwordReinit(id,key,password).map(status =>
        if (status > 0) Ok(success)
        else            BadRequest(unknownUserid)
      )}

    )

  }

}