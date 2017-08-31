package controllers
import com.typesafe.config._
import utilities._

/**
 * references: https://github.com/nscala-time/nscala-time
 * Always favor nscala over java.util.Date
 * If Java8 then the time API should be pretty good: http://stackoverflow.com/questions/24631909/differences-between-java-8-date-time-api-java-time-and-joda-time
 *
 */

object Date{

  /** 
   * imports
   */
  import java.util.Date
  import org.joda.time.DateTime
  import com.github.nscala_time.time.Imports._

  /**
   * convert from/to Date/DateTime
   */
  def datetimeToDate(d: DateTime):Date = new Date(d.getMillis)
  def dateToDatetime(d: Date):DateTime = new DateTime(d)


  /**
  * return current year
  *
  * @param date: Date
  * @return String of date
  */
  def year(date: Date = new java.util.Date()) : String = new java.text.SimpleDateFormat("yyyy").format(date)

  def strSql: java.sql.Date = {
    val cal:java.util.Calendar = java.util.Calendar.getInstance();
    val utilDate:java.util.Date = cal.getTime();
    new java.sql.Date(utilDate.getTime())
  }

  /**
   * Zulu: equivalent of UTC (Coordinated Universal Time / French: temps universel coordonné)
   * @see UTC https://en.wikipedia.org/wiki/Coordinated_Universal_Time
   * @see Zulu: http://stackoverflow.com/questions/9706688/what-does-the-z-mean-in-unix-timestamp-120314170138z
   * Zulu (short for "Zulu time") is used in the military and in navigation generally as a term for Universal Coordinated Time (UCT), sometimes called Universal Time Coordinated ( UTC ) or Coordinated Universal Time (but abbreviated UTC), and formerly called Greenwich Mean Time. In military shorthand, the letter Z follows a time expressed in Greenwich Time. Greenwich Time, now called Universal Coordinated Time, is the time at longitude 0 degrees 0 minutes - the prime meridian or longitudinal line that separates East from West in the world geographical coordinate system. This line of longitude is based on the location of the British Naval Observatory in Greenwich, England, near London. "Zulu" is the radio transmission articulation for the letter Z.
   * @return a format that is URL ready in the format: 20140711T091700Z
   */
  def url(d: Date) : String = str(d, Pattern.dateUrl, "Zulu")

  /**
   * add N hours to a date
   * @param n: number of hours to be added
   */
  def addNHour(date: Date, n: Int): Date = {
    val hour:Long = 3600*1000; // in milli-seconds.
    new Date(date.getTime() + n * hour);
  }

  def addNHour(date: DateTime, n: Int): DateTime = date+n.hours

  def addNDay(date: DateTime, n: Int):DateTime = date.plusDays(n)

  /**
   * sets timezone from string
   * @see list of available timezeones: http://joda-time.sourceforge.net/timezones.html
   * Some here: "Europe/Zurich", "Europe/Paris", "Europe/London", "America/New_York", "Asia/Singapore", "Asia/Hong_Kong"
   */
  def timezone(timezone: String = Pattern.timezone):DateTimeZone = DateTimeZone.forID(timezone)

  def strToDate(s: String, pattern: String = Pattern.date):java.util.Date = {
    import java.util.Date
    import java.text.SimpleDateFormat
    val format = new SimpleDateFormat(pattern, java.util.Locale.ENGLISH);

    format.parse(s)
  }

  /**
   * @return a date string with the right format and the right timezone
   * @param d: date to be converted
   * @param Pattern date pattern
   * @param timezone timezone , e.g. "America/Chicago"
   * @see additional info: java.util tmezone: http://tutorials.jenkov.com/java-date-time/java-util-timezone.html
   *
   */
  def str(date: Date, pattern:String = Pattern.date, timezone: String = Pattern.timezone):String = {
    import java.util._
    import java.text._
    val formatter = new SimpleDateFormat(pattern);
    val central   = TimeZone.getTimeZone(timezone);
    formatter.setTimeZone(central);
    formatter.format(date)
  }

  def dateToStr(date: DateTime, pattern:String = Pattern.date, timezone: String = Pattern.timezone):String = str(datetimeToDate(date), pattern, timezone)

  /**
   * reciprocal of dateToStr
   */
  def strToDateTime(date: String, pattern: String = Pattern.date, timezone: String = Pattern.timezone):DateTime = org.joda.time.format.DateTimeFormat
    .forPattern(pattern)
    .parseDateTime(date)
    .toDateTime(utilities.Date.timezone(timezone))

  /**
   * @param date
   * @return Json formatted date
   */
  def toJson(d: DateTime):String = dateToStr(d, Pattern.json)


  object Pattern{
    val timezone:String   = Constants.getConf("timezone",        "America/New_York")
    val date:String       = Constants.getConf("format.date",     "MM.dd.yyyy")
    val time:String       = Constants.getConf("format.time",     "HH:mm")
    val dateUrl:String    = Constants.getConf("format.dateUrl",  "yyyyMMdd'T'HHmmss'Z'")
    val json:String       = Constants.getConf("format.dateJson", "yyyy-MM-dd'T'HH:mm:ss'Z'")
    val datetime:String   = date+" "+time

    // date time for the plugin moment.js
    val datetime_moment:String  = "MM.DD.YYYY HH:mm"

    object Angular{
      val date            = "MM.dd.yyyy"
      val time            = "H:mm"
      val datetime        = date+" "+time
      val timezone        = "-0100"
    }
  }

  val formatter = org.joda.time.format.DateTimeFormat.forPattern(Pattern.date)
}