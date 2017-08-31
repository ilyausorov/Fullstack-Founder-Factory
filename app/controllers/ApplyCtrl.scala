package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.data.validation.Constraints._

import play.api.Play.current

import play.api.libs.json._

import javax.inject.Inject

import user_management.user.authentication.AuthenticationService
import user_management.user.models.{Login, Permission}
import play.api.i18n.{Lang, MessagesApi, Messages, I18nSupport}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import database._
import models._
import user_management.user.models._

class ApplyCtrl @Inject()(
  userService: user_management.user.UserManagementService,
  userExtendedService: UserExtended,
  userCompanyService: user_management.user.models.UserCompany,
  applyService: Apply,
  loginCtrl: LoginCtrl,
  helpers: user_management.play.utils.controller.Helper,
  actions: Actions
) (val messagesApi: MessagesApi) extends Controller with I18nSupport{
  import actions._

  def index() = Action{implicit request=>
  	Ok(views.html.admin_dashboard.applications.index())
  }
	
  def application() = Action{implicit request =>
    Ok(views.html.apply_page.application())
  }

  implicit val myJsonFormat0 = Json.format[OptionSet]
  implicit val myJsonFormat1 = Json.format[OptionSettwo]
  implicit val myJsonFormat2 = Json.format[UserExtendedRow]
  implicit val myJsonFormat3 = Json.format[UserFull]
  implicit val myJsonFormat4 = Json.format[ChoicesRow]
  implicit val myJsonFormat5 = Json.format[ApplicationQuestions]
  implicit val myJsonFormat6 = Json.format[ApplyRow]
  implicit val myJsonFormat7 = Json.format[ApplicationDetails]

  def listAdmin = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    applyService.listDetails.map{c =>
      Ok(Json.toJson(c))
    }
  }
	
  def deleteAdmin(id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    applyService.delete(id).flatMap{i => helpers.id(i)}
  }
	

  def adminViewApplication = (IsAuthenticated andThen IsAdmin) {implicit request =>
		helpers.single.bindFromRequest.fold(
		e => BadRequest("error"),
		v => Ok(views.html.admin_dashboard.applications.viewApplication(v))
	)
  }
	
  def update = Action.async{implicit request =>
    myForm.bindFromRequest.fold(
      e => Future(BadRequest(e.errorsAsJson)),
      v => v._1.map{application_id =>
	  	  if(v._6 == 2){
			  applyService.detail(application_id).flatMap{application_row=>
			  	applyService.update(
					application_row.get.copy(date_completed=Some(new org.joda.time.DateTime()), status_id = v._6)
				).flatMap{application_id=>
					ApplicationAnswers.updateMulti(v._5, application_id)
					helpers.id(application_id)
				}
			  }
		  }else{
				ApplicationAnswers.updateMulti(v._5, application_id)
				helpers.id(application_id)  
		  }
	  }.getOrElse{	
		    val company = user_management.user.models.CompanyRow(None, "")
			val user    = (new NewUser(v._4, utilities.encryption.Random.generate(8), Seq(2)))
			val userExt = database.UserExtendedRow(None, 0, v._2, v._3, Option(""))
        	userExtendedService.insert(user, userExt, company).flatMap{user_id =>
		  		applyService.update(
					ApplyRow(None, user_id, new org.joda.time.DateTime(), None, v._6, 0)
				).flatMap{application_id=>
					ApplicationAnswers.updateMulti(v._5, application_id)
					helpers.id(application_id)
				}
	  		}
		}
	)
  }
	
 def question_types = Action.async{implicit request =>
   QuestionTypes.listSelect.map{qtypes =>
      Ok(JsObject(Seq(
        "question_types" -> Json.toJson(qtypes)
      )))
    }
  }
	
 def application_headers = Action.async{implicit request =>
   ApplicationHeaders.listSelect.map{application_headers =>
      Ok(JsObject(Seq(
        "application_headers" -> Json.toJson(application_headers)
      )))
    }
  }	 

  def question_list(name: String) = Action.async{implicit request=>
  	applyService.question_list(name).map{r=>
		Ok(Json.toJson(r))	
       }	
  }	
	
  def find_application_id(email: String) = Action.async{implicit request=>
  	userService.find(email).flatMap{userrow=>
		applyService.find_by_user_id(userrow.get.id.get).flatMap{application_id=>
			helpers.id(application_id)
		}
	}
  }
	
  def detailApplication(id: Long)  = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    applyService.detailFull(id).map{application=>
		Ok(Json.toJson(application))
	}
  }
	
  def updateRead(id: Long) = (IsAuthenticated andThen IsAdmin).async{implicit request =>
	applyService.detail(id).flatMap{application=>
		applyService.update(application.get.copy(read_id=1)).flatMap{result=>
			helpers.id(result)
		}
	}  
  }
	
  def listAnswers(id: Long)	 = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    	applyService.detailbyApplication(id).map{r =>											   
      		r.map{x =>
				val intQs:Seq[(String, JsObject)]  = x.answers.filter(!_.multi).map{y  => y.key -> JsObject(Seq(
				  "id"     -> JsNumber(y.key.toInt),
				  "val"    -> JsNumber(y.value),
				  "olabel" -> Json.toJson(y.olabel)
				))}

				val multiQs:Seq[(String, JsValue)] = x.answers.filter(_.multi).groupBy(_.key).toList.map{case (k, v) =>
				   k match{case a => {
					  (a, JsObject(Seq("val"  -> JsObject(v.map{y =>
					  y.value.toString -> JsBoolean(true)
					}))))
				   }
				  }
				}
				Ok(
				  JsObject(Seq(
					"id"        -> JsNumber(x.id),
					"intQs"     -> JsObject(intQs),
					"multiQs"   -> JsObject(multiQs)
				  ))
				)
		  }.getOrElse(BadRequest("error"))
    }  
  }
	
	
  def thankyouemail(email: String, first_name: String) = Action.async{implicit request=>   
	val ce = views.html.email_templates.thankyouemail(first_name).body
	val e = new utilities.Email(Seq(email), "Confirmation: You submitted your application to the " + utilities.Constants.name, ce, true) 
	e.setFrom("Ilya from Fullstack Founder Factory <ilya@fullstackfounderfactory.com>")
	e.send
	
	helpers.id(1l)  
  }
	
  val answers = mapping(
    "id"             -> optional(longNumber),
    "application_id" -> default(longNumber, 0l),
    "qid"            -> nonEmptyText,
    "val"            -> longNumber,
    "olabel"         -> optional(nonEmptyText),
	"desc"           -> optional(nonEmptyText),
    "multi"          -> default(boolean, false)
  )(ApplicationAnswersRow.apply)(ApplicationAnswersRow.unapply)

  val myForm = Form(tuple(
    "application_id" -> optional(longNumber),
	"first_name"	 -> nonEmptyText,
	"last_name"	     -> nonEmptyText,
	"email"          -> email,
    "questions"      -> list(answers),
    "status_id"      -> default(longNumber, 1l)
  )) 
}