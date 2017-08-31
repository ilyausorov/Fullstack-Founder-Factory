package utilities
import com.typesafe.config._
import scala.BigDecimal

object Constants{

  val conf = ConfigFactory.load()
  def getConf(key: String, default: String =""):String     = if(conf.hasPath(key))(conf.getString(key))else(default)
  def getConf(key: String, default: Boolean):Boolean       = if(conf.hasPath(key))(conf.getBoolean(key))else(default)
  def getConf(key: String, default: Int):Int               = if(conf.hasPath(key))(conf.getInt(key))else(default)
  def getConf(key: String, default: BigDecimal):BigDecimal = if(conf.hasPath(key)) BigDecimal(conf.getString(key))else(default)
  def getConf(key: String, default: Char):Char             = if(conf.hasPath(key)){
    try{
      conf.getString(key).toCharArray match {
        case a => a(0)
      }
    }
    catch{ 
      case _:Throwable => default 
    }
  }else(default)

  val name  = getConf("name", "no_name")
  val email  = getConf("email", "no_email")
  val emailaddress = getConf("emailaddress", "no_email_address")
  /**
   *  maximum duration of a session
   */
  val sessionMaxDuration:Int  = getConf("sessionMaxDuration", 50*60*60)
  val online:Boolean          = getConf("online", false)

  /**
   * project url
   * 
   */
  val protocol:String         = getConf("protocol", "http")
  val urlWoPrefix:String      = getConf("urlWoPrefix", "no_website")
  val domain:String           = getConf("domain", "www")
  val url:String              = if(online)(protocol+"://"+domain+"."+urlWoPrefix)else("http://localhost:9000")

  /**
   * path/url do not end with /
   */
  val path:String       = getConf("pathy", "/srv/data")

  val lang:String       = getConf("lang", "en")

  val version:String     = getConf("version", "0.1")

  /**
   * definres locale for project 
   * @see http://www.oracle.com/technetwork/java/javase/javase7locales-334809.html
   */
  object Locale{
    val country:String     = getConf("locale.country", "US")
    val lang:String      = getConf("locale.lang", "en")
    lazy val locale     = new java.util.Locale(lang, country)
  }

  /**
   * information about Developer
   */
  object Developer{
    val name         = getConf("developer.name", "no_name")
    val url          = getConf("developer.url", "no_website")
    val email        = getConf("developer.email", "no_email")
    val address      = getConf("developer.address", "no_address")
    val zip          = getConf("developer.zip", "no_zip")
    val city         = getConf("devloper.city", "no_city")
    val country      = getConf("developer.country", "no_country")
  }

  /**
   * references to local resources
   */
  object Src{
    val bower        = "bower_components/"
  }

  val domains_reserved = List("www", "admin", "accounting", "strategie", "strategy", "stage", "test", "dev", "prod", "www3", "management", "people", "sp", "dataviz", "showcase", "s4e")

  object Twitter{
    val pid  = getConf("twitter.pid", "no_twitter")
    val id  = "@"+pid
    val url  = "https://www.twitter.com/"+id
  }
}