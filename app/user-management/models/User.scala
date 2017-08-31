package user_management.user.models

import user_management.user.database.Unique
import org.joda.time.DateTime

/**
 * Representation of a data-table row
 * @param id
 * @param company_id
 * @param username
 * @param bcryptedPassword
 * @param key
 * @param status_id
 * @param date_added
 * important: bcryptedoassword must NOT be the same as MD5 password
 */
case class UserRow(
  id: Option[Long]
  , company_id: Long
  , username: String
  , bcryptedPassword: String
  , md5Password: String
  , key: String
  , status_id: Long
  , lang: String = utilities.Constants.lang
  , date_added: Option[DateTime] = None
  , date_edit: Option[DateTime] = None 
) extends User

case class PermittedUser(
	id: Option[Long]
	, company_id: Long
	, username: String
	, bcryptedPassword: String
	, md5Password: String
	, key: String
	, status_id: Long
	, permissions:Seq[Long] = Seq()
) extends RestrictedUser {
	def toRow: UserRow = UserRow(id, company_id, username, bcryptedPassword, md5Password, key, status_id)
}

/**
 * Minimal User trait
 */
trait User extends Unique {
	val company_id: Long
	val username: String
	val bcryptedPassword:String
	val md5Password:String
	val key:String
	val status_id:Long
}

trait RestrictedUser extends User with Permissions {
	def toRow:UserRow
}


