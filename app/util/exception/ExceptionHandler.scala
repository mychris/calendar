package util

import scala.reflect.ClassTag

/**
  *
  * @author Simon Kaltenbacher
  */
package object exception {

  /** */
  def exceptionToEither[E <: Exception, A, B](block: => B, error: E => A)(implicit ct: ClassTag[E]): Either[A, B] =
    try {
      Right(block)
    }
    catch {
      case e: E => Left(error(e))
    }
}