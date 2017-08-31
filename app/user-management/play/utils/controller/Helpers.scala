package user_management.play.utils.controller

import javax.inject.Inject

import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.i18n.Messages
import play.api.i18n.MessagesApi
import play.api.libs.json.Format
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.mvc.Controller
import play.api.mvc.Request
import play.api.mvc.Result

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.json._

/**
 * Helper methods for Controllers
 */
class Helpers @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport{

  /**
   * Returns the first error as Json
   * @param request
   * @tparam T Type of the Form[T]
   * @return Mapping function Form[T] => Result
   */
  def errorToJson[T](implicit request: Request[AnyContent]):((Form[T]) => Future[Result]) = {
    e => 	errorToJson(e)
  }

  /**
   * Returns the first error as Json
   * @param e Form of Type T
   * @param request implicit Request for Language
   * @tparam T Type of Form[T]
   * @return Result
   */
  def errorToJson[T](e:Form[T])(implicit request: Request[AnyContent]):Future[Result] = {
    e.errors.head match {case (error) =>
      Future.successful(BadRequest(Json.obj("error" -> error.message,"detail" -> error.key,"message" -> Messages("error." + error.message+ {if (error.key.isEmpty) "" else "." + error.key}))))
    }
  }

  def error[A](e: play.api.data.Form[A]):Future[Result] = Future.successful{
    BadRequest(JsObject(e.errors.map{a =>
      a.key.replace(".","_") -> 
        JsObject(Seq(
          "message" -> JsString(Messages(a.message)),
          "detail"  -> JsString(a.message) 
        ))
    }))
  }

  /**
   * Returns all errors as Json
   * @param request
   * @tparam T Type of the Form[T]
   * @return Mapping function Form[T] => Result
   */
  def errorsToJson[T](implicit request: Request[AnyContent]):((Form[T]) => Future[Result]) = {
    e => errorsToJson(e)
  }

  /**
   * Returns all errors as Json
   * @param e Form of Type T
   * @param request
   * @tparam T Type of the Form[T]
   * @return Mapping function Form[T] => Result
   */
  def errorsToJson[T](e:Form[T])(implicit request: Request[AnyContent]):Future[Result] = Future.successful{
    BadRequest(Json.toJson(e.errors.map{error =>
      Json.obj("error" -> error.message,"detail" -> error.key,"message" -> Messages("error." + error.message + {if (error.key.isEmpty) "" else "." + error.key}))
    }))
  }

  /**
   * Returns 200 if a resource is present, otherwise 404
   * @param m optional Model to be returned
   * @param jsonFormat Format to return the JSON
   * @tparam T Type of model
   * @return 200 if present, 404 if not
   */
  def respondWithOptionalJson[T](m:Future[Option[T]],message:String)(implicit jsonFormat:Format[T]):Future[Result] = {

    m.map(_.map(model => Ok(Json.toJson(model))).getOrElse(NotFound(Json.obj("error" -> "not.found","detail" -> "not.found","message" -> message))))

  }

}
