package user_management.user.authentication

import user_management.user.models.RestrictedUser
import org.apache.commons.codec.binary.Base64
import play.api.mvc._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object BasicAuthentication {

  /**
   * Decode a Basic Authentication Header
   * @param authHeader
   * @return
   */
  def decodeBasicAuth(authHeader: String): (String, String) = {

    try {

      val Array(user, password) = new String(Base64.decodeBase64(authHeader.replaceFirst("Basic ", "")), "UTF-8").split(":")
      (user, password)

    } catch {

      case e: Exception => ("NO_USERNAME_FOUND","NO_PASSWORD_FOUND")
    }

  }

  /**
   * Responds with 401 unauthorized, if authentication fails
   * @param request Request with BA headers
   * @return Result
   */
  def onFailure(request: RequestHeader) = Results.Unauthorized.withHeaders("WWW-Authenticate" -> ("Basic realm=\"" + request.uri + "\""))
  def onBAFailure(request:RequestHeader) = Future.successful(onFailure(request))


  /**
   * Performs the Basic Authentication
   * @param f Function to evaluate the credentials with parameters (user,password)
   * @param request Request with BA headers (username and password)
   * @return User object of the requesting user
   */
  def onSuccess(f:(String,String)=>Future[Option[RestrictedUser]])(request: RequestHeader) = {
    request.headers.get("Authorization").map {decodeBasicAuth(_) match {case (user, password) => f(user,password)}}.getOrElse(Future{None})
  }

}

