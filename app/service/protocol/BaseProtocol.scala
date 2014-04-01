package service.protocol

import scala.reflect.ClassTag

/** Base trait for all requests sent to service actors
  *
  * @author Simon Kaltenbacher
  */
trait Request

/** Base trait for all requests sent to service actors
  *
  * @author Simon Kaltenbacher
  */
trait Response {

  /** */
  def fold[A : ClassTag, B](onError: Error => B, onSuccess: A => B): B = this match {
    case error: Error => onError(error)
    case response: A  => onSuccess(response)
    case _            => throw new Exception("Type of right value does not conform to supplied type!")
  }

  /** */
  def toEither[A : ClassTag]: Either[Error, A] = this match {
    case error: Error => Left(error)
    case response: A  => Right(response)
    case _            => throw new Exception("Type of right value does not conform to supplied type!")
  }
}

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

  def unapply(response: Response) = response match {
    case error: Error => Some(error.message)
    case _            => None
  }
}