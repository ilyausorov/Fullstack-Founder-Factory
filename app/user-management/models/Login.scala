package user_management.user.models

case class Login(
  username: String,
  password: String
)
extends Credentials