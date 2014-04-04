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
  def fold[S <: Success, A](onError: Error => A, onSuccess: S => A)(implicit ct: ClassTag[S]): A = this match {
    case error : Error => onError(error)
    case success: S    => onSuccess(success)
    case _             => throw new Exception("Type of right value does not conform to supplied type!")
  }

  /** */
  def toEither[S <: Success](implicit ct: ClassTag[S]): Either[Error, S] = this match {
    case error: Error => Left(error)
    case success: S   => Right(success)
    case _            => throw new Exception("Type of right value does not conform to supplied type!")
  }
}

/** Base trait for all success messages sent by service actors
  *
  * @author Simon Kaltenbacher
  */
trait Success extends Response

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