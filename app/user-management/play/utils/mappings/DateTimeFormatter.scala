package user_management.play.utils.mappings

import play.api.data.FormError
import play.api.data.format.Formatter

import org.joda.time.DateTime
import java.text.SimpleDateFormat

class DateTimeFormatter extends Formatter[DateTime] {
 
  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], DateTime] = {
    try{
      Right(new DateTime(data(key).toLong))
    }
    catch{
      case _:Throwable => Left(Seq(new FormError(key, Seq("error.date"))))
    }
  }
 
  override def unbind(key: String, value: DateTime): Map[String, String] = Map(key ->value.getMillis.toString)
}

