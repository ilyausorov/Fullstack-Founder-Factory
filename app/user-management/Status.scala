package user_management.user

object Status {
  lazy val list = Map("deactivated" -> 0L, "ok" -> 1L, "pending" -> 2L, "email_not_confirmed" -> 3L)
  // todo: revert
 
  lazy val default:Long = 2L
  lazy val ok:Long 		  = 1L
  lazy val deactivated  = 0L
  lazy val emailNotConfirmed = 3L
}