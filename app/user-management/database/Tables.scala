package user_management.user.database

import java.sql.Timestamp

import org.joda.time.DateTime
import slick.driver.MySQLDriver.api._
import slick.lifted.Tag

object JodaToSqlMapper {
	implicit val dateTimeToDate = MappedColumnType.base[DateTime, Timestamp](
	dateTime => new Timestamp(dateTime.getMillis),
	timestamp => new DateTime(timestamp))
}

abstract class IndexedTable[T](tag: Tag, name:String) extends Table[T](tag, name){
  def id: Rep[Long]
}

abstract class IndexedTableP[T](tag: Tag, name:String) extends IndexedTable[T](tag, name){
	def id:       Rep[Long]
	def position: Rep[Long]
}

abstract class IndexedTableUser[T](tag: Tag, name:String) extends IndexedTable[T](tag, name){
	def id:			  Rep[Long]
	def user_id:	Rep[Long]
}

abstract class IndexedTableUserP[T](tag: Tag, name:String) extends IndexedTableUser[T](tag, name){
	def id:			  Rep[Long]
	def user_id:	Rep[Long]
	def position: Rep[Long]
}

abstract class IndexedTableUserPT[T](tag: Tag, name:String) extends IndexedTableUserP[T](tag, name){
	def id:			  Rep[Long]
	def user_id:	Rep[Long]
	def position: Rep[Long]
	def type_id: 	Rep[Long]
}

abstract class IndexedTableUserManagement[T](tag: Tag, name:String) extends IndexedTable[T](tag, name){
	def id:			          Rep[Long]
	def username:         Rep[String]
	def bcryptedPassword:	Rep[String]
	def md5Password:      Rep[String]
	def key:		          Rep[String]
  def status_id:        Rep[Long]
  def lang:             Rep[String]
	def date_added:	      Rep[Option[DateTime]]
}

abstract class IndexedTableCompany[T](tag: Tag, name:String) extends IndexedTable[T](tag, name){
	def id:			    Rep[Long]
	def company_id:	Rep[Long]
}