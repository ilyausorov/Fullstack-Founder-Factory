package database

import user_management.user.database._

import slick.driver.MySQLDriver.api._
import slick.lifted.{TableQuery, Tag}
import slick.model.ForeignKeyAction
import org.joda.time.DateTime
import user_management.user.database.JodaToSqlMapper._

class ApplyDB(tag: Tag) extends IndexedTable[ApplyRow](tag, "apply"){
  def id          		  = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id     		  = column[Long]("user_id")
  def date_started		  = column[DateTime]("date_started")
  def date_completed	  = column[Option[DateTime]]("date_completed")
  def status_id			  = column[Long]("status_id")
  def read_id			  = column[Long]("read_id")

  def user_fk     = foreignKey("position_user_fk", user_id, TableQuery[user_management.user.UserDB])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  def * = (id.? , user_id, date_started, date_completed, status_id, read_id) <> ((ApplyRow.apply _).tupled, ApplyRow.unapply _)
}

case class ApplyRow (
  id:         Option[Long],
  user_id:    Long,
  date_started: DateTime,
  date_completed: Option[DateTime],
  status_id: Long,
  read_id: Long = 0
) extends Unique


case class ApplicationAnswersRow(
  id: Option[Long],
  application_id: Long,
  key: String,
  value: Long,
  olabel: Option[String] = None,
  desc: Option[String] = None,
  multi: Boolean = false
) extends Unique

class ApplicationAnswersDB(tag: Tag) extends IndexedTable[ApplicationAnswersRow](tag, "application_answers"){
  def id             = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def application_id = column[Long]("application_id")
  def key            = column[String]("keyy")
  def value          = column[Long]("valuey")
  def olabel         = column[Option[String]]("olabel")
  def desc           = column[Option[String]]("desc")
  def multi          = column[Boolean]("multi")

  def * = (id.? , application_id, key, value, olabel, desc, multi) <> ((ApplicationAnswersRow.apply _).tupled, ApplicationAnswersRow.unapply _)
}

class QuestionTypesDB(tag: Tag) extends IndexedTable[QuestionTypesRow](tag, "question_types"){
  def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def nametype      = column[String]("nametype", O.SqlType("varchar(255)"))

  def * = (id.? , nametype) <> ((QuestionTypesRow.apply _).tupled, QuestionTypesRow.unapply _)
}

case class QuestionTypesRow (
  id:       Option[Long],
  nametype:     String
) extends Unique

class ApplicationHeadersDB(tag: Tag) extends IndexedTable[ApplicationHeadersRow](tag, "applicationheaders"){
  def id        = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name      = column[String]("name", O.SqlType("varchar(255)"))
  def description = column[Option[String]]("description")
  def collection_id = column[String]("collection_id")
  def step = column[Long]("step")

  def * = (id.? , name, description, collection_id, step) <> ((ApplicationHeadersRow.apply _).tupled, ApplicationHeadersRow.unapply _)
}

case class ApplicationHeadersRow (
  id:       Option[Long],
  name:     String,
  description: Option[String],
  collection_id: String,
  step: Long
) extends Unique

class QuestionsDB(tag: Tag) extends IndexedTable[QuestionsRow](tag, "questions"){
  def id             = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def question       = column[Option[String]]("question", O.SqlType("text"))
  def description    = column[Option[String]]("description")
  def question_type          = column[Long]("question_type")
  def header        = column[Long]("header")

  def * = (id.? , question, description, question_type, header) <> ((QuestionsRow.apply _).tupled, QuestionsRow.unapply _)
}

case class QuestionsRow(
  id: Option[Long],
  question: Option[String],
  description: Option[String] = None,
  question_type: Long,
  header: Long
) extends Unique


class CollectionDB(tag: Tag) extends IndexedTable[CollectionRow](tag, "collection"){
  def id             = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def collection_id  = column[String]("collection_id")
  def question_id    = column[Long]("question_id")
  def question_order         = column[Long]("question_order")
	
  def * = (id.? , collection_id, question_id, question_order) <> ((CollectionRow.apply _).tupled, CollectionRow.unapply _)
}

case class CollectionRow(
  id: Option[Long],
  collection_id: String,
  question_id: Long,
  question_order: Long
) extends Unique

class ChoicesDB(tag: Tag) extends IndexedTable[ChoicesRow](tag, "choices"){
  def id             = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def question_id    = column[Long]("question_id")
  def choice_value          = column[Long]("choice_value")	
	def choice_text          = column[Option[String]]("choice_text")	
	
  def * = (id.? , question_id, choice_value, choice_text ) <> ((ChoicesRow.apply _).tupled, ChoicesRow.unapply _)
}

case class ChoicesRow(
  id: Option[Long],
  question_id: Long,
  choice_value: Long,
  choice_text: Option[String]
) extends Unique
