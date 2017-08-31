package controllers
import javax.inject.Inject
import user_management.user._
import user_management.user.actions._
import user_management.user.authentication._
import user_management.user.models._
import play.api.libs.json.Json
import play.api.mvc. {
	Result,
	RequestHeader,
	Results
}
import user_management.play.utils.xml.XMLValidator
class Actions @Inject()(validator: XMLValidator, userManagementService: UserManagementService) {
	private def onUnauthorized(r: RequestHeader) = Results.Redirect(routes.LoginCtrl.index()).withNewSession.flashing("redirect" -> "loggedout")
	private def onForbidden(user: RestrictedUser) = Results.Ok(views.html.error_page.index("Access forbidden"))
	private def onNoToken = Results.Unauthorized(Json.obj("error" -> "invalid.csrf.token"))
	private def onBasicAuthenticationFailed(r: RequestHeader) = BasicAuthentication.onFailure(r)
	val checkUser = new CheckUserIsDefined(_)
	val IsAuthenticated = new FindUserByTokenOrBySession(userManagementService) andThen checkUser(onUnauthorized)
	val AuthenticateBasic = new FindUserByBA(userManagementService) andThen checkUser(onBasicAuthenticationFailed)
	val WithPermission = new CheckPermission(onForbidden)(_)
	val IsAdmin = WithPermission(Seq(Permission.Admin))
	val IsUser = WithPermission(Seq(Permission.User, Permission.Admin))
}