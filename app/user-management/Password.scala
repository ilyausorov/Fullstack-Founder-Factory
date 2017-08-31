package user_management.user

import utilities.encryption.Md5


object Password {

  import org.mindrot.jbcrypt.BCrypt

  /**
   * Hash a password with Salt
   * @param password
   * @return
   */
  def hash(password: String) = BCrypt.hashpw(password, BCrypt.gensalt())

  /**
   * Check password against hash
   * @param hash
   * @param password
   * @return
   */
  def check(hash: String, password: String) = BCrypt.checkpw(password, hash)

  def checkMd5(hash:String,password:String) = Md5.hash(password) == hash

}