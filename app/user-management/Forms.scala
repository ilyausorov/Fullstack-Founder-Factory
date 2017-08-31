package user_management.user

import play.api.data.Form

object Forms {

  lazy val login              = Form(Mappings.login)
  lazy val changeStatus       = Form(Mappings.changeStatus)
  
  lazy val changePassword     = Form(Mappings.changePassword)
  lazy val resetPassword      = Form(Mappings.resetPassword)
  lazy val passwordAdmin      = Form(Mappings.newPassword)
  lazy val passwordReinit     = Form(Mappings.passwordReinit)

  lazy val changeUserName     = Form(Mappings.changeUserName)
  lazy val newUser            = Form(Mappings.newUser.verifying("company_id.missing", result => result match{case(u) => u.company_id.isDefined}))
  lazy val newUserWithCompany = Form(Mappings.signUpCompanyWithUser)
  lazy val newUserWithCompanyAndExtUser = Form(Mappings.signUpCompanyWithUserExtended)

  lazy val deleteUser         = Form(Mappings.deleteUser)
  lazy val changePermission   = Form(Mappings.changePermission)

  lazy val userCompany        = Form(Mappings.userCompany)

}
