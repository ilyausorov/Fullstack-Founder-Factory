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

case class CrudRoutes(list: play.api.mvc.Call, detail: play.api.mvc.Call, update: play.api.mvc.Call, delete: play.api.mvc.Call)

/**
 * Helper methods for Controllers
 */
class Helper @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport{

  /**
   * Returns all errors as Json
   * @param request
   * @tparam T Type of the Form[T]
   * @return Mapping function Form[T] => Result
   */
  def error[A](e: play.api.data.Form[A]):Future[Result] = Future.successful{
    BadRequest(JsObject(e.errors.map{a =>
      a.key.replace(".","_") -> JsObject(Seq(
        "message" -> JsString(Messages(a.message)),
        "detail"  -> JsString(a.message) 
      ))
    }))
  }

  import play.api.data.Forms._
  import play.api.data.Form

  val single = Form(
    play.api.data.Forms.single("id" -> longNumber)
  )

  val singleO = Form(
    play.api.data.Forms.single("id" -> optional(longNumber))
  )

  val double = Form(
    tuple(
      "id1" -> longNumber,
      "id2" -> longNumber
    )
  )

  val multi = Form(
    play.api.data.Forms.single("ids" -> list(longNumber))
  )

  val position = Form(tuple(
    "id"    -> longNumber,
    "delta"   -> number
  ))

  def error(s: String = "Error"):Future[Result] = Future.successful(
    NotFound(seqToJson(Seq(("status", s))))
  )

  def ok(s: String = "Ok"):Future[Result] = Future.successful(
    Ok(seqToJson(Seq(("status", s))))
  )

  def id(id: Long, status: Int = 200):Future[Result] = {
    val r = JsObject(
      Seq(
        "id"      -> JsNumber(id)
      )
    )

    Future.successful(
      status match {
        case 201 => play.api.mvc.Results.Created(r)
        case _   => Ok(r)
      }
    )
  }

  def boolean(b: Boolean) = Ok(
    Json.toJson(JsObject(
      Seq(
        "r"      -> {if(b){JsNumber(1)}else{JsNull}}
      )
    ))
  )

  def login(authenticate: (String, String) => Boolean) = Form(
    tuple(
      "email"    -> text,
      "password"  -> text
    )
    .verifying("error.login.nomatch", result => result match{
      case(a, b) => {authenticate(a,b)}
    })
  )

  /*
    form for new passwords
  */
  val passwordNew = tuple(
      "a"  -> nonEmptyText
    , "b"  -> nonEmptyText
  )
  .verifying("error.password.mustMatch", result => result match{ case(a, b) => a == b  })

  val passwordNewId = Form(
    tuple(
        "id"  -> longNumber
      , "new" -> passwordNew
    )
  )

  val passwordForgot = Form(
    tuple(
      "id" -> longNumber
      , "key" -> nonEmptyText
      , "new" -> passwordNew
    )
  )

  def password(check: (String) => Boolean) = Form(
    tuple(
      "old" -> text
      .verifying("error.password.oldWrong", result => result match{
        case (old)=> check(old)
      }),
      "new" -> passwordNew
    )
  )

  def seqToJson(v: Seq[(String, String)]) = JsObject(v.map{case (k,v) =>
    k -> JsString(v)
  }.toList)

}
