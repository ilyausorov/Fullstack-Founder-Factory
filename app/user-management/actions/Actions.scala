package user_management.user.actions

import javax.inject.Inject
import user_management.play.utils.xml._
import user_management.user.UserManagementService
import user_management.user.authentication.BasicAuthentication
import user_management.user.models.{PermittedUser, RestrictedUser}
import play.api.mvc._

import scala.concurrent.Future
import scala.xml.NodeSeq
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Abstract class for more generic usage
 * @param request
 * @tparam A
 */
abstract class UserRequest[A](val request: Request[A]) extends WrappedRequest[A](request: Request[A]){
  val user:RestrictedUser
}

/**
 * Extension of request
 * Usage: val name = request.user.name
 * @param user
 * @param request
 * @tparam A
 */
class RequestWithUser[A](val user: RestrictedUser, request: Request[A]) extends UserRequest[A](request)

/**
 * Default user, if none is found in the database
 * Note: id is none in this case
 */
object unAuthorizedUser {
  def apply() = PermittedUser(None,0,"","","","",0)
}

/**
 * Extend the request with a user object upon session authentication
 */
class FindUserBySession @Inject()(userService:UserManagementService) extends ActionBuilder[UserRequest] with ActionTransformer[Request, UserRequest] {

  def transform[A](request: Request[A]) =  userService.findRestrictedUser(request.session.get("username").getOrElse("")).map(u => new RequestWithUser(u.getOrElse(unAuthorizedUser()), request))

}

/**
 * Extend the request with a user object upon basic authentication
 */
class FindUserByBA @Inject()(userService:UserManagementService) extends ActionBuilder[UserRequest] with ActionTransformer[Request, UserRequest] {

  def transform[A](request: Request[A]) =  {BasicAuthentication.onSuccess(userService.authenticateWebService(_,_))(request)}.map(u => new RequestWithUser(u.getOrElse(unAuthorizedUser()),request))

}

class FindUserByToken @Inject()(userService: UserManagementService) extends ActionBuilder[UserRequest] with ActionTransformer[Request, UserRequest]{
  def transform[A](request: Request[A]) = request.headers.get("X-Auth-Token")
    .map{token =>
      userService.findRestrictedUserByToken(token)
      .map((u: Option[RestrictedUser]) =>
        new RequestWithUser(
          u.getOrElse(unAuthorizedUser()), request
        )
      )
    }
    .getOrElse(Future(new RequestWithUser(unAuthorizedUser(), request)))
}

class FindUserByTokenOrBySession @Inject()(userService: UserManagementService) extends ActionBuilder[UserRequest] with ActionTransformer[Request, UserRequest]{
  def transform[A](request: Request[A]) = request.headers.get("X-Auth-Token")
    .map{token =>
      userService.findRestrictedUserByToken(token)
      .map((u: Option[RestrictedUser]) =>
        new RequestWithUser(
          u.getOrElse(unAuthorizedUser()), request
        )
      )
    }
    .getOrElse(userService.findRestrictedUser(request.session.get("username").getOrElse("")).map(u => new RequestWithUser(u.getOrElse(unAuthorizedUser()), request)))
}

/**
 * Filter for authenticated users
 * @param onUnauthorized Result to be returned if not authorized
 */
class CheckUserIsDefined(onUnauthorized: RequestHeader => Result) extends ActionFilter[UserRequest] {
  def filter[A](input: UserRequest[A]) = Future.successful {
    input.user.id.map(_ => None).getOrElse(Some(onUnauthorized(input)))
  }
}

/**
 * Check for permissions
 * @param onForbidden Result on access denied
 * @param permissions Permissions needed to access
 */
class CheckPermission(onForbidden: RestrictedUser => Result)(permissions:Seq[Long]= Seq()) extends ActionFilter[UserRequest] {
  def filter[A](input: UserRequest[A]) = Future.successful {
    if (permissions.isEmpty || input.user.permissions.intersect(permissions).nonEmpty) None else Some(onForbidden(input.user))
  }
}

/**
 * Filter to check the CSRF-Token sent by Javascript/Angular
 * @param onFailure HTTP-Response on failure
 */
class CheckCSRFToken(onFailure: => Result) extends ActionFilter[UserRequest] {
  def filter[A](request: UserRequest[A]): Future[Option[Result]] = Future.successful{

    val tokenFromCookie:String = request.cookies.get("XSRF-TOKEN").map(_.value).getOrElse("cookieUndefined")
    val tokenFromCustomHeader:String = request.headers.get("X-XSRF-TOKEN").getOrElse("headerUndefined")

    //GET is not vulnerable to CSRF and will only complicate the fetching of HTML-Templates
    if (request.method == "GET" || tokenFromCookie == tokenFromCustomHeader) None else Some(onFailure)

  }
}

/**
 * Check XML-request against an XSD after authentication
 * @param path
 * @param onFailure
 */
class ValidateXMLUser @Inject()(validator:XMLValidator) (path:String,onFailure: String => Result)  extends ActionFilter[UserRequest] {
  def filter[A](request: UserRequest[A]): Future[Option[Result]] = Future.successful{

    val check = if (request.body.isInstanceOf[NodeSeq]) validator.validate(path,request.body.asInstanceOf[NodeSeq]) else Some("no XML found")
    check.map(error => onFailure(error)).orElse(None)

  }
}

/**
 * Check an XML-request against an XSD
 * @param path
 * @param onFailure
 */
class HasValidXML @Inject()(validator:XMLValidator) (path:String,onFailure: String => Result)  extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] ={

    val check = if (request.body.isInstanceOf[NodeSeq]) validator.validate(path,request.body.asInstanceOf[NodeSeq]) else Some("no XML found")
    check.map(error => Future.successful(onFailure(error))).getOrElse(block(request))

  }
}
