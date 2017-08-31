import javax.inject.Inject

import utilities.BugReporter
import user_management.play.utils.pushover.Pushover
import play.api.Logger
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc._
import play.api.mvc.Results._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global


class ErrorHandler @Inject()(pushover: Pushover) extends HttpErrorHandler {

  val bugReporter = new BugReporter(Seq(utilities.Constants.name+" "+"<"+utilities.Constants.email+">"))

  def onClientError(request: RequestHeader, statusCode: Int, message: String) = {
    Future.successful(
      request.contentType match {
        case Some("application/json") =>  Status(statusCode)(Json.obj("ERROR" -> "client.error","DETAIL" -> Json.toJson("A client error occurred. A client error occurred. Please try again."),"message" -> Json.toJson("A client error occurred.")))
        case Some("application/xml")  => Status(statusCode)(<error>A client error occurred. Please try again.</error>)
        case _                        => Status(statusCode)(views.html.error_page.index("error"))
      }
    )
  }

    def onServerError(request: RequestHeader, exception: Throwable) = {

    Logger.error("Internal Server Error in " + request.path,exception)

    Future.successful(

      request.contentType match {
        case Some("application/json") =>  InternalServerError(Json.obj("ERROR" -> "server.error","DETAIL" -> "internal server error","message" -> "Parameter missing or Internal Server Error"))
        case Some("application/xml") => InternalServerError(<serverError>An error occured. Please try again.</serverError>)
        case _  => InternalServerError(views.html.error_page.index("error"))
      }
    ) andThen {case _ => bugReporter.send(exception,request.path,pushover.push)}
  }
}
