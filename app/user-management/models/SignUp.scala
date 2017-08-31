package user_management.user.models

import utilities.encryption.Md5
import user_management.user.{Status, Secret, Password}
import database.user.models.NewUser
import user_management.user.Status

case class SignUp(
    username: String
  , password: String
  , permissions: Seq[Long]            = Seq()
  , webservicePassword:Option[String] = None
  , company_id: Option[Long]          = None
  , status_id: Long                   = Status.default

) extends NewUser {
  override def toRow: UserRow =
    UserRow(
        None
      , company_id.getOrElse(0L)
      , username
      , Password.hash(password)
      , Md5.hash(webservicePassword.getOrElse(Secret.randomKey))
      , Secret.randomKey
      , status_id
  )
}