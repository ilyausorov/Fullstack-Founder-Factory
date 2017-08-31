package user_management.play.utils.mappings

import play.api.data.FieldMapping
import play.api.data.format.Formatter

object Mappings {

  def of[DateTime](implicit binder: Formatter[DateTime]): FieldMapping[DateTime] = FieldMapping[DateTime]()(binder)
  val jodaDateTime = of(new DateTimeFormatter())

  val jodaDateIso = of(new DateTimeIsoFormatter())

}



