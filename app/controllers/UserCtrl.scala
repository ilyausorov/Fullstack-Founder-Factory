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
import user_management.user.models. {
	Login,
	Permission
}
import play.api.i18n. {
	Lang,
	MessagesApi,
	Messages,
	I18nSupport
}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import user_management.user.models._
import database._

class UserCtrl @Inject()(
	userService: user_management.user.UserManagementService,
	userExtendedService: models.UserExtended,
	userCompanyService: UserCompany,
	helper: user_management.play.utils.controller.FormHelper,
	actions: Actions
)(val messagesApi: MessagesApi) extends Controller with I18nSupport {

	import actions._
	implicit val myJsonFormat0 = Json.format[UserRow]
	implicit val myJsonFormat1 = Json.format[CompanyRow]
	implicit val myJsonFormat2 = Json.format[database.UserExtendedRow]
	implicit val myJsonFormat3 = Json.format[models.UserFull]
	
	def index = (IsAuthenticated andThen IsAdmin) {
		implicit request =>
			Ok(views.html.admin_dashboard.user.index())
	}
	def edit = (IsAuthenticated andThen IsAdmin) {
		implicit request =>
			helper.single.bindFromRequest.fold(
				e => Redirect(routes.UserCtrl.add),
				v => Redirect(routes.UserCtrl.editM(v))
			)
	}
	def editM(id: Long) = editT(Some(id))
	def add = (IsAuthenticated andThen IsAdmin) {
		implicit request =>
			Ok(views.html.admin_dashboard.user.add())
	}
	def editT(id: Option[Long] = None) = (IsAuthenticated andThen IsAdmin) {
		implicit request =>
			Ok(views.html.admin_dashboard.user.edit(id))
	}
	/* SERVICE */
	def listAdmin = IsAuthenticated.async {
		implicit request =>
			userService.list.map {
				u =>
					Ok(Json.toJson(u))
			}
	}
	def detail = IsAuthenticated.async {
		implicit request =>
			userExtendedService.detail(request.user.id.get).map {
				s =>
					Ok(Json.toJson(s))
			}
	}
	def detailCandidate(candidate_id: Long) = Action.async {
		implicit request =>
			userExtendedService.detail(candidate_id).map {
				s =>
					Ok(Json.toJson(s))
			}
	}
	def findRestrictedUser(username: String) = IsAuthenticated.async {
		implicit request =>
			userService.findRestrictedUser(username).map {
				ouser =>
					ouser.map {
						user =>
							user.id.map {
								id =>
									Ok(Json.toJson(id))
							}.getOrElse(Ok("fail"))
					}.getOrElse(Ok("fail"))
			}
	}
	def detailAdmin(id: Long) = (IsAuthenticated andThen IsAdmin).async {
		implicit request =>
			userService.detail(id).flatMap {
				ou =>
					ou.map {
						u =>
							userExtendedService.listSingle(u.id.get).flatMap {
								ocandidate =>
									userExtendedService.listSingle(u.id.get).map {
										oemployer =>
											val jcandidate = ocandidate.map {
												candidate =>
													JsObject(Seq("candidate" ->  Json.toJson(candidate)))
											}.getOrElse(JsObject(Seq("" ->  JsNull)))
										val jemployer = oemployer.map {
											employer =>
												JsObject(Seq("employer" ->  Json.toJson(employer)))
										}.getOrElse(JsObject(Seq("" ->  JsNull)))
										Ok(Json.toJson(u).as[JsObject]++jcandidate++jemployer)
									}
							}
					}.getOrElse(Future(Ok(JsNull)))
			}
	}
	def generatePasswordAdmin(id: Long) = (IsAuthenticated andThen IsAdmin).async {
		implicit request =>
			val password = utilities.encryption.Random.generate(8)
		userService.changePassword(id, password).map {
			status =>
				if (status > 0) Ok(password)
			else BadRequest
		}
	}
	def deleteAdmin(id: Long) = (IsAuthenticated andThen IsAdmin).async {
		implicit request =>
			userService.delete(id).flatMap {
				u =>
					helper.ok()
			}
	}
  def insertAdmin = (IsAuthenticated andThen IsAdmin).async{implicit request =>
    userForm.bindFromRequest.fold(
      e => Future(BadRequest(e.errorsAsJson)),
      v => userService.update(v.copy(id = None, date_added = Some(new org.joda.time.DateTime()))).flatMap{x => 
         userCompanyService.update(UserCompanyRow(None, x, v.company_id)).flatMap{xx =>
          helper.id(x)
        }
      }
    )
  }
	val userForm = play.api.data.Form(
		user_management.user.Mappings.user
		.verifying("error.email.taken", result => result match {
			case (a) =>
			import scala.concurrent._
			import scala.concurrent.duration._
			!Await.result(userService.exists(a.username, a.id), 5000 millis)
		})
	)
}