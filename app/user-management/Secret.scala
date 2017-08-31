package user_management.user

import utilities.encryption.Random

/**
 * Create new secret
 **/
object Secret{
  def randomKey = Random.alphanumeric(16)

  /**
   * Omit I, l, 0 and O to avoid confusion among clients
   * @return
   */
  def randomKeyNoIl0O:String = {

    val alphabet = "ABCDEFGHJKLMNPQRSTUVWXYSabcdefghijkmnopqrstuvwxyz123456789"
    Random.randomString(alphabet)(10)

  }
}
