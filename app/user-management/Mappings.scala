package user_management.user

import user_management.play.utils.mappings.Mappings._
import user_management.user.models._
import play.api.data.Form
import play.api.data.Forms._


object Mappings {

	val login =
    mapping(
			"username" -> text,
			"password" -> text
	  )(Login.apply)(Login.unapply _)

  /**
   * form for new passwords
   * 
   */
  val newPassword = tuple(
      "new1" -> nonEmptyText
    , "new2" -> nonEmptyText
  )
  .verifying("error.password.mustMatch", result => result match{ case(a, b) => a == b })

	val resetPassword =
		tuple(
			"user_id" -> longNumber,
			"new"    -> newPassword
		)

	val changePassword =
		tuple(
			"password" -> text,
			"new"      -> newPassword
    )

	val passwordReinit =
		tuple(
			  "id"    -> longNumber
			, "key"   -> nonEmptyText
			, "new"   -> newPassword
		)

	val changeStatus =
		tuple(
				"id"     -> longNumber
			, "key"    -> nonEmptyText
			, "status" -> longNumber()
		)

	val changeUserName = single("username" -> nonEmptyText)

  val newUser =
    mapping(
				"username"            -> nonEmptyText
			,	"password"            -> nonEmptyText
			, "permissions"         -> seq(longNumber)
			,	"webservicePassword"  -> optional(text)
			, "company_id"          -> optional(longNumber)
			, "status_id"           -> default(longNumber, Status.default)
		)(SignUp.apply)(SignUp.unapply _)
			.verifying("no.permission.assigned", result => result match{
			case(fields) => fields.permissions.length > 0
		})

  val user = mapping(
    "id"                    -> optional(longNumber),
    "company_id"            -> longNumber,
    "username"              -> nonEmptyText,
    "bcryptedPassword"      -> nonEmptyText,
    "md5Password"           -> nonEmptyText,
    "key"                   -> nonEmptyText,
    "status_id"             -> longNumber,
    "lang"                  -> nonEmptyText,
    "date_added"            -> optional(jodaDate),
    "date_edited"           -> optional(jodaDate)
  )(user_management.user.models.UserRow.apply)(user_management.user.models.UserRow.unapply _)

	val extendedUser = mapping(
		"id" -> optional(longNumber)
		, "user_id" -> longNumber
		, "firstName"-> text
		, "lastName" -> text
		, "email"-> text
		, "middleName" -> optional(text)
		, "sex" -> optional(number)
		, "birthdate" -> optional(jodaDateTime)
		, "nationality" -> optional(longNumber)
	)(UserExtRow.apply)(UserExtRow.unapply _)

	val company = mapping(
	  "id"                    -> optional(longNumber),
    "name"                  -> nonEmptyText,
    "domain"                -> optional(text),
    "date_added"            -> optional(jodaDate("yyyy-MM-dd"))
	)(CompanyRow.apply)(CompanyRow.unapply _)

	val signUpCompanyWithUser =
		tuple(
			"company"               -> company
			, "user"                -> newUser
		)
	val signUpCompanyWithUserExtended =
		tuple(
			  "company"             -> company
			, "user"                -> newUser
			, "userExtended"        -> extendedUser
		)

	val deleteUser = single("user_id" -> longNumber)

	val changePermission = tuple("user_id" -> longNumber,"permission_id" -> longNumber).verifying("invalid.permission_id", result => result match {
		case (user_id,permission_id) => Permission.Permissions.contains(permission_id)
	})

  val userCompany = mapping(
    "id" -> optional(longNumber)
    , "user_id" -> longNumber
    , "company_id" -> longNumber
    , "role" -> optional(text)
  )(UserCompanyRow.apply)(UserCompanyRow.unapply _)

}
