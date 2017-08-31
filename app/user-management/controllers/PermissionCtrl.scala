package user_management.user.controllers

import javax.inject.Inject

import user_management.play.utils.controller.Helpers
import user_management.user.actions.StandardActions
import user_management.user.models.{Permission, UserPermission}
import user_management.user.{UserManagementService, Forms}
import play.api.libs.json.Json
import play.api.mvc.Controller

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PermissionCtrl @Inject()(
  userPermissionService: UserPermission,
  userService: UserManagementService,
  actions: StandardActions,
  helpers: Helpers) extends Controller {
import actions._

  lazy val success = Json.obj("status" -> "success")
  lazy val onUnknownUser = BadRequest(Json.obj("error" -> "unknown.user_id"))

  implicit val myJsonFormat = Json.format[Permission]

  def assign = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    Forms.changePermission.bindFromRequest.fold(
      e => helpers.errorToJson(e),
      v => v match{case (user_id, permission_id) =>
        userService.detail(user_id).flatMap(s => 
          s.map(user =>
            {userPermissionService.assign(Permission(Some(permission_id),""))(user)}.map(_ =>
              Ok(success)
            )
          )
          .getOrElse(Future.successful(onUnknownUser))
        )
      }
    )
  }

  def revoke = (IsAuthenticated andThen IsAdmin).async{implicit request =>

    Forms.changePermission.bindFromRequest.fold(
      e => helpers.errorToJson(e),
      v=> v match{case (user_id, permission_id) =>
        userService.detail(user_id).flatMap(s =>
          s.map(user =>
            {userPermissionService.revoke(Permission(Some(permission_id),""))(user)}.flatMap(_ =>
              Future.successful(Ok(success))
            )
          )
          .getOrElse(Future.successful(onUnknownUser))
        )
      }
    )
  }

  /**
   * list of permissions with assigned or not
   * @param id: user_id
   */
  def listWithAssign(id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    userPermissionService.listWithAssign(id).map{p =>
      Ok(Json.toJson(p))
    }
  }

}
