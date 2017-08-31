package models

import user_management.user.database._
import database._
import user_management.user.models._

import play.api.Play
import play.api.Play.current
import play.api.mvc.Request
import slick.driver.MySQLDriver
import slick.lifted.TableQuery
import org.joda.time.DateTime
import javax.inject.Inject

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import slick.driver.MySQLDriver.api._

case class ApplicationAnswers(
	id: Long,
	answers: Seq[ApplicationAnswersRow]
)

case class ApplicationQuestions(
  id: Option[Long],
  question: Option[String],
  description: Option[String],
  question_type: Long,
  question_order: Long,
  header: Long,
  choices: Seq[ChoicesRow]
)

case class ApplicationDetails(
	id: Option[Long],
	user_id: Long,
	date_started: DateTime,
	date_completed: Option[DateTime],
	status_id: Long,
	read_id: Long,
	email: String,
	first_name: String,
	last_name: String
)

class Apply @Inject()(
  userService: user_management.user.UserManagementService,
  userCompanyService: user_management.user.models.UserCompany,
  userExtendedService: UserExtended
) extends CRUD[ApplyDB, ApplyRow] {
  override val query = TableQuery[ApplyDB]

	def find_by_user_id(id: Long):Future[Long] = {
		list.map{applyrow=>
			applyrow.filter(x=> x.user_id == id).map{specificrow=>
				specificrow.id.get
			}.lift(0).get
		}
	}
	
	def listDetails:Future[Seq[ApplicationDetails]] = {
		val fResults = for{
			x <- userExtendedService.listPublic
			y <- list
		} yield (x,y)
		
		fResults.map{case (users, applications) =>
			applications.flatMap{app=>
				users.filter(_.id == app.user_id).map{user=>
					ApplicationDetails(
						app.id,
						user.id,
						app.date_started,
						app.date_completed,
						app.status_id,
						app.read_id,
						user.email,
						user.user.firstName,
						user.user.lastName
					)
				}			
			}	
		}	
	}
	
	def detailFull(id: Long):Future[Option[ApplicationDetails]] = {
		val fResults = for{
			x <- userExtendedService.listPublic
			y <- detail(id)
		} yield (x,y)
		
		fResults.map{case (users, applications) =>
			applications.flatMap{app=>
				users.filter(_.id == app.user_id).headOption.map{user=>
					ApplicationDetails(
						app.id,
						user.id,
						app.date_started,
						app.date_completed,
						app.status_id,
						app.read_id,
						user.email,
						user.user.firstName,
						user.user.lastName
					)
				}			
			}	
		}	
	}
	
	def detailbyApplication(id: Long):Future[Option[ApplicationAnswers]] = {
		val fResults = for{
			x <- detail(id)
			y <- ApplicationAnswers.list(id)
		}
		yield(x, y)
	
		fResults.map {case (application, answers) =>
			application.map{app=>
				ApplicationAnswers(
					app.id.get,
					answers
				)
			}
		}
	}
	
	
	def question_list(name: String):Future[Seq[ApplicationQuestions]] = {	
		val fResults = for{
			x <- Questions.list 
			y <- Collection.list
			z <- Choices.list
		} yield(x, y, z)

		fResults.map{case (ques, col, choices) =>
			col.filter(_.collection_id == name).flatMap{onlycol =>							
				ques.filter(_.id == Some(onlycol.question_id)).map{onlyques =>
					ApplicationQuestions(
						onlyques.id,
						onlyques.question, 
						onlyques.description, 
						onlyques.question_type, 
						onlycol.question_order,
						onlyques.header,
						choices.filter(_.question_id == onlycol.question_id)
					)
				}
			}
		}					  
	}
}

object Questions extends CRUD[QuestionsDB, QuestionsRow] {
  override val query = TableQuery[QuestionsDB]	
}

object Collection extends CRUD[CollectionDB, CollectionRow]{
  override val query = TableQuery[CollectionDB]
}

object Choices extends CRUD[ChoicesDB, ChoicesRow]{
  override val query = TableQuery[ChoicesDB]
}

object ApplicationAnswers extends CRUD[ApplicationAnswersDB, ApplicationAnswersRow] {
	override val query = TableQuery[ApplicationAnswersDB]
	
	def listing = db.run {
		query.result
	}
	
	def list(application_id: Long) = db.run {
		query.filter(_.application_id === application_id).result
	}
	
	def updateMulti(v: Seq[ApplicationAnswersRow], application_id: Long) {
		deleteAll(application_id).map {x => 
			Thread.sleep(500L)
			v.map {w =>
				update(w.copy(application_id = application_id))
			}
		}
	}
	
	def deleteAll(application_id: Long) = db.run {
		deleteQuery(x => x.application_id === application_id)
	}
	
}

object QuestionTypes extends CRUD[QuestionTypesDB, QuestionTypesRow] {
  override val query = TableQuery[QuestionTypesDB]

  def listSelect = list.map{fx =>
    fx.map{x=>
      OptionSet(x.id.get, x.nametype)
    }
  }
}

case class OptionSettwo(id: Long, name: String, label: Option[String] = None, collection_id: String, step: Long)

object ApplicationHeaders extends CRUD[ApplicationHeadersDB, ApplicationHeadersRow] {
  override val query = TableQuery[ApplicationHeadersDB]

  def listSelect = list.map{fx =>
    fx.map{x=>
      OptionSettwo(x.id.get, x.name, x.description, x.collection_id, x.step)
    }
  }
}
