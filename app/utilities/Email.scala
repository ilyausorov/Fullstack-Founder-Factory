package utilities

import java.io.ByteArrayOutputStream
import javax.activation.{FileDataSource, DataHandler}
import javax.mail._
import javax.mail.internet._
import java.util.Properties
import javax.mail.util.ByteArrayDataSource
import scala.collection.JavaConversions._

import scala.collection.mutable.ArrayBuffer

case class Attachment(data:Array[Byte], mimeType:String,name:String)

/**
 * the class is based on the Java Mail API and allows to send emails
 *
 * @see https://javamail.java.net/nonav/docs/api/
 * @see http://alvinalexander.com/source-code/scala/scala-send-email-class-uses-javamail-api
 */

class Email(
  to: Seq[String],
  subject: String,
  content: String,
  html: Boolean = false,
  attachments:Seq[Attachment] = Seq.empty
){

  /**
   * recipients list
   *
   */
  val recipients:ArrayBuffer[(String, javax.mail.Message.RecipientType)] = ArrayBuffer()
  /**
   * initialisation
   */
  setTo(to)

  /**
   * 
   *
   */
  def setRecipients(rs: Seq[String], t: Message.RecipientType = Message.RecipientType.TO) = {
    rs.map{r =>  (r, t)+=:recipients}
  }

  /**
   * add additional recipients
   */
  def setTo(to: Seq[String])    = setRecipients(to, Message.RecipientType.TO)

  /**
   * add CC recipients
   * 
   */
  def setCC(cc: Seq[String])    = setRecipients(cc, Message.RecipientType.CC)

  /**
   * add BCC recipients
   *
   */
  def setBCC(bcc: Seq[String])  = setRecipients(bcc, Message.RecipientType.BCC)


  def setToCcBccRecipients {
    recipients.foreach{case (recipient,recipientType) =>
      val addressArray = InternetAddress.parse(recipient).asInstanceOf[Array[javax.mail.Address]]
      if ((addressArray != null) && (addressArray.length > 0)){
        message.addRecipient(recipientType,addressArray.head)
      }
    }
  }

  /**
   * Ininitalisation of default from email address
   */
  var from:String = Constants.getConf("mail.smtp.from")

  /**
   * change from address
   */
  def setFrom(f: String){ 
		val g = f.replace(",","")
		from = g
}

/**
   * Ininitalisation of reply to address
   */
  var replyTo:String = Constants.getConf("mail.smtp.replyTo")

  /**
   * change from address
   */
  def setReplyTo(f: String){ 
		val g = f.replace(",","")
		replyTo = g
}
  /**
   * Message
   */
  val message: Message = {
    val properties = new Properties()

    properties.put("mail.smtp.host", Constants.getConf("mail.smtp.host", "localhost"))
    /**
     * SMTP by default uses TCP port 25. The protocol for mail submission is the same, but uses port 587. SMTP connections secured by SSL, known as SMTPS, default to port 465 (nonstandard, but sometimes used for legacy reasons).
     */
    properties.put("mail.smtp.port", Constants.getConf("mail.smtp.port", 25).toString)

    val pauth = Constants.getConf("mail.smtp.auth", false)
    properties.put("mail.smtp.auth", pauth.toString)

    val auth    = if(pauth){
      Some(
        new javax.mail.Authenticator {
          protected override def getPasswordAuthentication() = new PasswordAuthentication(Constants.getConf("mail.smtp.username"), Constants.getConf("mail.smtp.password"))
        }
      )
    }
    else{
      None
    }

    val session = Session.getInstance(properties, auth.getOrElse(null))
    new MimeMessage(session)
  }

  private def setContentWithAttachments() = {

      val multiPartMessage = new MimeMultipart()

      val mimeContent = new MimeBodyPart()
      if (html) mimeContent.setContent(content, "text/html; charset=utf-8") else mimeContent.setText(content)
      multiPartMessage.addBodyPart(mimeContent)

      attachments.foreach(a => {

        val mimeAttachment = new MimeBodyPart()
        val dataSource = new ByteArrayDataSource(a.data,a.mimeType)
        val dataHandler = new DataHandler(dataSource)
        mimeAttachment.setDataHandler(dataHandler)
        mimeAttachment.setFileName(a.name)
        multiPartMessage.addBodyPart(mimeAttachment)

      })

      message.setContent(multiPartMessage)

  }

  val setContent = () => if(html)message.setContent(content, "text/html; charset=utf-8") else  message.setText(content)

  /**
   * sends message
   */
  def send:Boolean = {
    try{
      message.setFrom(new InternetAddress(from))
        if(replyTo != ""){
	  		  message.setReplyTo(Array(new InternetAddress(replyTo)));
		}

	  setToCcBccRecipients

      message.setSentDate(new java.util.Date())
      message.setSubject(subject)

      if (attachments.isEmpty) setContent() else setContentWithAttachments()
	
      Transport.send(message)
      true
    }
    catch{
      case _:Throwable => false
    }
  }
}