package database.user.models

import user_management.user.models.{Permissions, UserRow, Credentials}

trait NewUser extends Credentials with Permissions {
  def toRow:UserRow
}