package user_management.user.actions

import javax.inject.Inject

import user_management.play.utils.xml.XMLValidator
import user_management.user.UserManagementService
import user_management.user.authentication.BasicAuthentication
import user_management.user.models.{Permission, RestrictedUser}
import play.api.libs.json.Json
import play.api.mvc.{RequestHeader, Result, Results}

class StandardActions @Inject()(validator:XMLValidator, userManagementService: UserManagementService)  {

  private def onForbidden = Results.Forbidden(Json.obj("error" -> "action.forbidden"))
  private def onForbidden(user:RestrictedUser):Result = onForbidden

  private def onUnauthorized(user:RequestHeader) = Results.Unauthorized(Json.obj("error" -> "unauthorized"))
  private def onNoToken = Results.Unauthorized(Json.obj("error" -> "invalid.csrf.token"))
  private def onBAFailed(r:RequestHeader) = BasicAuthentication.onFailure(r)


  val IsAuthenticated    = new FindUserBySession(userManagementService) andThen new CheckUserIsDefined(onUnauthorized) andThen new CheckCSRFToken(onNoToken)
  val AuthenticateBasic  = new FindUserByBA(userManagementService) andThen new CheckUserIsDefined(onBAFailed)
  val IsAuthToken        = new FindUserByToken(userManagementService)  andThen new CheckUserIsDefined(onBAFailed)

  val WithPermission     = new CheckPermission(onForbidden)(_)

  val IsAdmin            = WithPermission(Seq(Permission.Admin))
  val IsUser             = WithPermission(Seq(Permission.User,Permission.Admin))

}
