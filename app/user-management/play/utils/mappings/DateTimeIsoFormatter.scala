package user_management.play.utils.mappings

import java.text.SimpleDateFormat

import org.joda.time.DateTime
import play.api.data.FormError
import play.api.data.format.Formatter

/**
 * same as above but for ISO date format
 */
class DateTimeIsoFormatter extends Formatter[DateTime] {

  val dateFormat = new SimpleDateFormat(utilities.Date.Pattern.json)

  override def bind(key: String, data: Map[String, String]): Either[Seq[FormError], DateTime] = try{
    Right(new DateTime(dateFormat.parse(data(key))))
  }
  catch{
    case _:Throwable => Left(Seq(new FormError(key, Seq("error.date"))))
  }

  override def unbind(key: String, value: DateTime): Map[String, String] = Map(key -> dateFormat.format(new java.util.Date(value.getMillis)))
}
