package utilities.encryption

object Random{
	// random password pgeneration
	// stolen from     http://www.bindschaedler.com/2012/04/07/elegant-random-string-generation-in-scala/

	// Random generator
	val random = new scala.util.Random(new java.security.SecureRandom())
	val alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYSabcdefghijklmnopqrstuvwxyz0123456789"

	/**
   * Generate a random string of length n from the given alphabet
   */
	def randomString(alphabet: String)(n: Int): String = Stream.continually(random.nextInt(alphabet.size)).map(alphabet).take(n).mkString

	/**
   * generate a random alpha numreric string 
   */
	def alphanumeric(n: Int) = randomString(alphabet)(n)

	/**
   * generates password
   * @param n: length of output password
   */
	def generate(n:Int = 5):String = {
		import java.security.SecureRandom;
		import java.math.BigInteger;

		val random = new SecureRandom()
		return new BigInteger(130, random).toString(32).substring(0,n)
	}
}