package user_management.user.authentication

import user_management.user.UserManagementService
import user_management.user.actions.UserRequest
import user_management.user.models.{RestrictedUser, Constants, Credentials}
import com.google.inject.Inject
import play.api.cache.CacheApi
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class AuthenticationService @Inject()(userService:UserManagementService,cache:CacheApi) {

  /**
   * Login function for any arbitrary credentials
   * @param credentials username and password
   * @param onSuccess target-url, if authentication was successful
   * @param onUnauthorized Action, if authentication failed
   * @param request implicit request
   * @return
   */
  def login(credentials: Credentials,onSuccess: => Call,onUnauthorized: => Result)(implicit request:Request[AnyContent]):Future[Result] = login(credentials,user => onSuccess,onUnauthorized)

  /**
   * Login function for any arbitrary credentials
   * @param credentials username and password
   * @param onSuccess target-url, if authentication was successful
   * @param onUnauthorized Action, if authentication failed
   * @param request implicit request
   * @return
   */
  def login(credentials: Credentials,onSuccess: RestrictedUser => Call,onUnauthorized: => Result)(implicit request:Request[AnyContent]):Future[Result] = {

    userService.authenticate(credentials.username,credentials.password).map(_.map(user => {

      Results.Redirect(onSuccess(user)).withSession("username" -> user.username).withCookies(Cookie("XSRF-TOKEN",request.cookies.get("XSRF-TOKEN").map(_.value).getOrElse("cookieUndefined"), httpOnly = false))

    }).getOrElse(onUnauthorized))


  }

  /**
   * Logout a user by destroying its session
   * @param onLogout Action to be performed after logout (message, redirect etc.)
   * @param userRequest UserRequest, required to clean the cache
   * @return
   */
  def logout(onLogout: => Result)(implicit userRequest: UserRequest[AnyContent]):Result = {
    cache.remove(Constants.cacheKey + userRequest.user.username)
    onLogout.withNewSession
  }

  /*** TOKEN ***/


  /**
   * set token to User, generates token and insert into to DB
   * @param u: RestrictedUser
   */
  def setToken(u: user_management.user.models.RestrictedUser): String = {
    val token = play.api.libs.Crypto.generateToken
    userService.changeWebServicePassword(u.id.get, token, false)
    token
  }

  /**
   * set token to user Credentials
   * @param  credentials
   * @todo DO NOT use webservice password for that! and expiration
   * @return token
   */
  def setToken(credentials: Credentials, duration: org.joda.time.Duration):Future[Option[String]] = userService.authenticate(credentials.username, credentials.password).map{fu =>
    fu.map{u =>
      setToken(u)
    }
  }

  /**
   * renew token, based on existin gtoken
   * @param  token: existing valid token
   * @return new token
   */
  def renewToken(token: String):Future[Option[String]] = userService.findRestrictedUserByToken(token).map{fu =>
    fu.map{u =>
      setToken(u)
    }
  }
}
