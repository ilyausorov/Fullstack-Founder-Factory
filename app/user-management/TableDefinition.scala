package user_management.user

import user_management.user.database._
import user_management.user.models._
import org.joda.time.DateTime
import com.github.tototoshi.slick.MySQLJodaSupport._
import slick.driver.MySQLDriver.api._
import slick.lifted.{TableQuery, Tag}
import slick.model.ForeignKeyAction



class PermissionDB(tag: Tag) extends IndexedTable[Permission](tag, "permission") {
  def id           = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name         = column[String]("name", O.SqlType("varchar(20)"))

  def * = (id.? , name, false) <> ((models.Permission.apply _).tupled, models.Permission.unapply _)
}

class UserDB(tag: Tag) extends IndexedTableUserManagement[UserRow](tag, "user") {
  def id                  = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def company_id          = column[Long]("company_id")
  def username            = column[String]("email",O.SqlType("varchar(254)"))
  def bcryptedPassword    = column[String]("password_bcrypt",O.SqlType("varchar(254)"))
  def md5Password         = column[String]("password_md5",O.SqlType("varchar(254)"))
  def key                 = column[String]("keyy",O.SqlType("varchar(256)"))
  def status_id           = column[Long]("status")
  def lang                = column[String]("lang")
  def date_added:slick.lifted.Rep[Option[org.joda.time.DateTime]] = column[Option[DateTime]]("date_added")
  def date_edited:slick.lifted.Rep[Option[org.joda.time.DateTime]] = column[Option[DateTime]]("date_edited")

  def company_fk = foreignKey("user_company_fk", company_id, TableQuery[CompanyDB])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  def idx_email = index("idx_email", (username), unique = true)

  def * = (id.? , company_id, username, bcryptedPassword,md5Password, key, status_id, lang, date_added, date_edited) <> ((UserRow.apply _).tupled, UserRow.unapply _)
}

class UserPermissionDB(tag: Tag) extends IndexedTableUser[UserPermissionRow](tag, "user_permission") {
  def id                = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id           = column[Long]("user_id")
  def permission_id     = column[Long]("permission_id")

  // here no cascade - wanna prevent deleting  
  def user_fk           = foreignKey("user_user_permission_fk", user_id, TableQuery[UserDB])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  def permission_fk     = foreignKey("permission_user_permission_fk", permission_id, TableQuery[PermissionDB])(_.id)

  def * = (id.? , user_id, permission_id) <> ((UserPermissionRow.apply _).tupled, UserPermissionRow.unapply _)
}

class CompanyDB(tag: Tag) extends IndexedTable[CompanyRow](tag, "company"){
  def id               = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name             = column[String]("name",O.SqlType("varchar(254)"))
  def domain           = column[Option[String]]("domain",O.SqlType("varchar(254)"))
  def date_added       = column[Option[DateTime]]("date_added")
  def * = (id.? , name, domain, date_added) <> ((CompanyRow.apply _).tupled, CompanyRow.unapply _)
}

class UserCompanyDB(tag: Tag) extends IndexedTableUser[UserCompanyRow](tag, "user_company"){
  def id              = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id         = column[Long]("user_id")
  def company_id      = column[Long]("company_id")
  def role            = column[Option[String]]("role")

  // todo: check cascades
  def user_fk         = foreignKey("user_company_user_fk", user_id, TableQuery[UserDB])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  def company_fk      = foreignKey("user_company_company_fk", company_id, TableQuery[CompanyDB])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)

  def * = (id.? , user_id, company_id, role) <> ((UserCompanyRow.apply _).tupled, UserCompanyRow.unapply _)
}

// todo: generate SQL code
class UserAddressDB(tag: Tag) extends IndexedTableUser[UserAddressRow](tag, "user_address"){
  def id              = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id         = column[Long]("user_id")
  def street          = column[String]("street",O.SqlType("varchar(254)"))
  def zip             = column[String]("zip",O.SqlType("varchar(254)"))
  def city            = column[String]("city",O.SqlType("varchar(254)"))
  def street2         = column[Option[String]]("street2",O.SqlType("varchar(254)"))
  def street3         = column[Option[String]]("street3",O.SqlType("varchar(254)"))
  def street4         = column[Option[String]]("street4",O.SqlType("varchar(254)"))
  def country_id      = column[Long]("country_id")

  def user_fk         = foreignKey("address_user_fk", user_id, TableQuery[UserDB])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  
  def * = (id.? , user_id, street, zip, city, street2, street3, street4, country_id) <> ((UserAddressRow.apply _).tupled, UserAddressRow.unapply _)
}

// todo: generate SQL code
class CompanyAddressDB(tag: Tag) extends IndexedTableCompany[CompanyAddressRow](tag, "company_address"){
  def id              = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def company_id      = column[Long]("company_id")
  def street          = column[String]("street",O.SqlType("varchar(254)"))
  def zip             = column[String]("zip",O.SqlType("varchar(254)"))
  def city            = column[String]("city",O.SqlType("varchar(254)"))
  def street2         = column[Option[String]]("street2",O.SqlType("varchar(254)"))
  def street3         = column[Option[String]]("street3",O.SqlType("varchar(254)"))
  def street4         = column[Option[String]]("street4",O.SqlType("varchar(254)"))
  def country_id      = column[Long]("country_id")

  def company_fk      = foreignKey("address_company_fk", company_id, TableQuery[CompanyDB])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)

  def * = (id.? , company_id, street, zip, city, street2, street3, street4, country_id) <> ((CompanyAddressRow.apply _).tupled, CompanyAddressRow.unapply _)
}

class UserExtDB(tag: Tag) extends IndexedTableUser[UserExtRow](tag, "user_extended"){
  def id              = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id         = column[Long]("user_id")
  def firstName       = column[String]("first_name",O.SqlType("varchar(254)"))
  def lastName        = column[String]("last_name",O.SqlType("varchar(254)"))
  def email           = column[String]("email",O.SqlType("varchar(254)"))
  def middleName      = column[Option[String]]("middle_name",O.SqlType("varchar(254)"))
  def sex             = column[Option[Int]]("sex")
  def birthdate       = column[Option[DateTime]]("birthdate")
  def nationality_id  = column[Option[Long]]("country_id")

  def user_fk         = foreignKey("usr_ext_fk", user_id, TableQuery[UserDB])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  
  def * = (id.? , user_id, firstName, lastName, email, middleName, sex, birthdate, nationality_id) <> ((UserExtRow.apply _).tupled, UserExtRow.unapply _)
}


class UserSocialDB(tag: Tag) extends IndexedTableUser[UserSocial](tag, "user_social") {
  def id            = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id       = column[Long]("user_id")
  def social_id     = column[Long]("social_id")
  def url           = column[String]("url")

  def user_fk       = foreignKey("user_social_fk", user_id, TableQuery[UserDB])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  def * = (id.?, user_id, social_id, url) <> ((UserSocial.apply _).tupled, UserSocial.unapply)
}

class UserThreadDB(tag: Tag) extends IndexedTableUser[UserThread](tag, "user_thread"){
  def id            = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id       = column[Long]("user_id")
  def login_id      = column[Long]("login_id")
  def comment       = column[String]("comment",O.SqlType("text"))
  def status_id     = column[Long]("status")
  def date_added    = column[DateTime]("date_added")

  def user_fk       = foreignKey("user_thread_user_fk", user_id, TableQuery[UserDB])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  def login_fk      = foreignKey("user_thread_admin_fk", login_id, TableQuery[UserDB])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  def * = (id.? , user_id, login_id, comment, status_id, date_added) <> ((UserThread.apply _).tupled, UserThread.unapply)
}

//case class Tag(id: Option[Long], name: String) extends Unique

class TagLabelDB(tag: Tag) extends IndexedTable[TagLabel](tag, "tag"){
  def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name        = column[String]("name",O.SqlType("varchar(254)"))

  def * = (id.? ,  name) <> ((TagLabel.apply _).tupled, TagLabel.unapply _)
}

class UserTagDB(tag: Tag) extends IndexedTableUser[UserTag](tag, "user_tag"){
  def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id     = column[Long]("user_id")
  def tag_id      = column[Long]("tag_id")

  def user_fk     = foreignKey("user_tag_user", user_id, TableQuery[UserDB])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  def tag_fk      = foreignKey("user_tag_tag", tag_id, TableQuery[TagLabelDB])(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)

  def * = (id.? , user_id, tag_id) <> ((UserTag.apply _).tupled, UserTag.unapply _)
}

class UserEmailDB(tag: Tag) extends IndexedTableUser[UserEmail](tag, "user_email"){
  def id              = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id         = column[Long]("user_id")
  def type_id         = column[Long]("country_id")
  def email           = column[String]("email",O.SqlType("varchar(254)"))

  def user_fk         = foreignKey("user_email_fk", user_id, TableQuery[UserDB])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  
  def * = (id.? , user_id, type_id, email) <> ((UserEmail.apply _).tupled, UserEmail.unapply _)
}

class UserPhoneDB(tag: Tag) extends IndexedTableUser[UserPhone](tag, "user_phone"){
  def id              = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def user_id         = column[Long]("user_id")
  def type_id         = column[Long]("type_id")
  def phone           = column[String]("phone")

  def user_fk         = foreignKey("user_phone_fk", user_id, TableQuery[UserDB])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)
  
  def * = (id.? , user_id, type_id, phone) <> ((UserPhone.apply _).tupled, UserPhone.unapply _)
}

class CompanyFileDB(tag: Tag) extends IndexedTableCompany[CompanyFile](tag, "company_file"){
  def id          = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def company_id  = column[Long]("company_id")
  def key         = column[String]("keyy")
  def name        = column[String]("name")
  def contentType = column[Option[String]]("content_type")
  def type_id     = column[Long]("type_id")
  def date_added  = column[DateTime]("date_added")

  def company_fk  = foreignKey("filec_company_fk", company_id, TableQuery[CompanyDB])(_.id, onUpdate=ForeignKeyAction.Cascade, onDelete=ForeignKeyAction.Cascade)

  def * = (id.? , company_id, key, name, contentType, type_id, date_added) <> ((CompanyFile.apply _).tupled, CompanyFile.unapply _) 
}

