package user_management.user.models

import user_management.user.{UserAddressDB, CompanyAddressDB}
import user_management.user.{UserExtDB}
import user_management.user.{UserSocialDB}
import user_management.user.{UserThreadDB}
import user_management.user.{UserPhoneDB}
import user_management.user.{UserEmailDB}
import user_management.user.{TagLabelDB, UserTagDB}
import user_management.user.database.{Unique, CRUD}
import user_management.user.database.{UniqueUser, CRUDByUser, CRUDByUser11}
import user_management.user.database.{UniqueCompany, CRUDByCompany}

import org.joda.time.DateTime

import slick.lifted.TableQuery

/**
 * `n` addresses can associated with users
 * @see http://www.upu.int/fileadmin/documentsFiles/activities/addressingAssistance/paperAddressingAddressingTheWorldAnAddressForEveryoneEn.pdf
 * @see http://stackoverflow.com/questions/929684/is-there-common-street-addresses-database-design-for-all-addresses-of-the-world
 */
case class UserAddressRow(
  id: Option[Long],
  user_id: Long,
  street: String,
  zip: String,
  city: String,
  street2: Option[String] = None,
  street3: Option[String] = None,
  street4: Option[String] = None,
  country_id: Long        = UserAddress.countryDefault
) extends UniqueUser

object UserAddress extends CRUDByUser[UserAddressDB, UserAddressRow]{
	val query = TableQuery[UserAddressDB]

  // work with countries in DataService (1 = Switzerland)
  val countryDefault:Long = 1
}

/**
 * `n` addresees can be associated with companies
 */
case class CompanyAddressRow(
  id: Option[Long],
  company_id: Long,
  street: String,
  zip: String,
  city: String,
  street2: Option[String] = None,
  street3: Option[String] = None,
  street4: Option[String] = None,
  country_id: Long        = UserAddress.countryDefault
) extends UniqueCompany

object CompanyAddress extends CRUDByCompany[CompanyAddressDB, CompanyAddressRow]{
  val query = TableQuery[CompanyAddressDB]
}

case class UserExtRow(
    id: Option[Long]
  , user_id: Long
  , firstName:String
  , lastName:String
  , email: String
  , middleName: Option[String]  = None
  , sex: Option[Int]            = None
  , birthdate: Option[DateTime] = None
  , nationality: Option[Long]   = None
) extends UniqueUser

object UserExt extends CRUDByUser11[UserExtDB, UserExtRow]{
  val query = TableQuery[UserExtDB]
}

case class UserSocial(id: Option[Long], user_id: Long, social_id: Long, url: String) extends UniqueUser

object UserSocial extends CRUDByUser[UserSocialDB, UserSocial]{
  val query = TableQuery[UserSocialDB]
}

case class UserThread(
  id: Option[Long]
  , user_id: Long
  , login_id: Long
  , comment: String
  , status_id: Long = 0l
  , date_added: DateTime = new DateTime()
) extends UniqueUser

object UserThread extends CRUDByUser[UserThreadDB, UserThread]{
  val query = TableQuery[UserThreadDB]
}

case class UserTag(
  id: Option[Long],
  user_id: Long,
  tag_id: Long
) extends UniqueUser

case class TagLabel(id: Option[Long], name: String) extends Unique

object TagLabel extends CRUD[TagLabelDB, TagLabel]{
  val query = TableQuery[TagLabelDB]
}

case class UserEmail(
  id: Option[Long],
  user_id: Long,
  type_id: Long,
  email: String
) extends UniqueUser

object UserEmail extends CRUDByUser[UserEmailDB, UserEmail]{
  val query = TableQuery[UserEmailDB]
}

case class UserPhone(
  id: Option[Long],
  user_id: Long,
  type_id: Long,
  phone: String
) extends UniqueUser

object UserPhone extends CRUDByUser[UserPhoneDB, UserPhone]{
  val query = TableQuery[UserPhoneDB]
}