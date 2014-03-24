package app.service

/** Base trait for all error messages sent by service actors
  *
  * @author Simon Kaltenbacher
  */
trait Error {

	/** The error's message */
	val message: String
}

/** Enables pattern matching on instances of [[app.service.Error]]
  *
  * @author Simon Kaltenbacher
  */
object Error {

	def unapply(value: Any) = value match {
		case error: Error => Some(error.message)
		case _						=> None
	}
}