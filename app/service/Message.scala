package service

/** Base trait for all requests sent to service actors
  *
  * @author Simon Kaltenbacher
  */
trait Request

/** Base trait for all requests sent to service actors
  *
  * @author Simon Kaltenbacher
  */
trait Response

/** Base trait for all error messages sent by service actors
  *
  * @author Simon Kaltenbacher
  */
trait Error extends Response {

  /** The error's message */
  val message: String
}

/** Enables pattern matching on instances of [[service.Error]]
  *
  * @author Simon Kaltenbacher
  */
object Error {

  def unapply(value: Any) = value match {
    case error: Error => Some(error.message)
    case _            => None
  }
}