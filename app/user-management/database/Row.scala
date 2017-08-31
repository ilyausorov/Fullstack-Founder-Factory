package user_management.user.database

trait Row[T] {
  def toRow[T]:T
}