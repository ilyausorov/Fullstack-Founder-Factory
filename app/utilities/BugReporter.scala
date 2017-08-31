package utilities

import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

/**
 *
 * @param recipients If not specified set to Constants.Developer
 * @param sender If not specified set to Constants.email
 */
class BugReporter(recipients:Seq[String] = Seq(), sender:String = ""){

  val to:Seq[String]         = if (recipients.isEmpty) Seq(Constants.Developer.email) else recipients
  val from:String            = if (sender.isEmpty) "BugReporter <" + Constants.email + ">" else sender

  /**
   * Send an exception via Email
   * @param exception Throwable
   * @param url causing url (r:RequestHeader => request.path)
   * @param push optional function to send a push notification. First parameter is the subject, second parameter the message
   * @return
   */
  def send(exception: Throwable, url: String = "", push:(String,String) => Future[Any] = (subject,title) =>Future(Unit)):Future[Boolean] = {

    lazy val stackTrace = exception.getStackTrace
    lazy val firstLine = stackTrace.headOption.getOrElse(new StackTraceElement("","","",0))
    lazy val subject = "Error at " + Constants.name
    lazy val message = "New Error occured in " + url + "\n"+
                       exception.getMessage + "\n" +
                       stackTrace.filter(l => l.getLineNumber >= firstLine.getLineNumber & l.getLineNumber < firstLine.getLineNumber+5 ).mkString("\n")
     if(Constants.online){
       push(subject,message).map{_ =>
         val email = new Email(
           to,
           subject,
           message
         )
         email.setFrom(from)
         email.send
       }
    }
    else{
      Future(true)
    }
  }
}