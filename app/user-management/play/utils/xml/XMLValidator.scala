package user_management.play.utils.xml


import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import javax.xml.validation.{Validator=>JValidator}
import com.google.inject.Inject
import org.xml.sax.SAXException

import java.io._

import play.api.cache.CacheApi
import scala.concurrent._
import scala.xml.{NodeSeq}

class XMLValidator @Inject() (cache:CacheApi)  {

  private def doValidation(xsdPath:String,xml:StreamSource):Option[String] = {

    try {
      val schemaLang = "http://www.w3.org/2001/XMLSchema"
      val factory = SchemaFactory.newInstance(schemaLang)

      val schemaFile = cache.getOrElse(xsdPath, duration.DurationInt(24*3600) seconds)  {

        new StreamSource(xsdPath)
      }

      val schema = factory.newSchema(schemaFile)

      val validator = schema.newValidator()
      validator.validate(xml)
      None

    } catch {
      case ex: SAXException => println("validation error: " + ex.getMessage); Option(ex.getMessage)
      case ex: Exception => ex.printStackTrace(); Option(ex.getMessage)
    }

  }

  def apply(xsdPath: String, xml:NodeSeq): Boolean = doValidation(xsdPath,new StreamSource(new StringReader(xml.toString))).map(_ => false).getOrElse(true)

  def apply(xsdPath: String, filePath:String): Boolean = doValidation(xsdPath, new StreamSource(filePath)).map(_ => false).getOrElse(true)

  /**
   * Validate an xml against an XSD and return the error, if failed
   * @param xsdPath
   * @param xml
   * @return None if valid, Option of Error if invalid
   */
  def validate(xsdPath: String, xml:NodeSeq):Option[String] = doValidation(xsdPath,new StreamSource(new StringReader(xml.toString)))
  def validate(xsdPath: String, filePath:String):Option[String] = doValidation(xsdPath, new StreamSource(filePath))

  def validate(xsdPath: Seq[String], xml:NodeSeq, result:Option[String] = Some("No XSD supplied")):Option[String] = {
    if (xsdPath.isEmpty | result.isEmpty) result
    else validate(xsdPath.tail,xml, doValidation(xsdPath.head, new StreamSource(new StringReader(xml.toString))))
  }

}