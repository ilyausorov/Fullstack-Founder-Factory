package user_management.user.database

/**
 * trait to indicate that the case class is unique
 */
trait Unique {
	val id: 		Option[Long]
}

trait UniquePosition extends Unique{
  val position:   Long
}

trait UniqueUser extends Unique{
	val user_id: Long
}

trait UniqueUserPosition extends UniqueUser{
  val position:   Long
}

trait UniqueUserPositionType extends UniqueUserPosition{
  val type_id:  Long
}

trait UniqueCompany extends Unique{
	val company_id: Long
}

trait UniqueCompanyPosition extends UniqueUser{
  val position:   Long
}

trait UniqueCompanyPositionType extends UniqueCompanyPosition{
  val type_id:  Long
}