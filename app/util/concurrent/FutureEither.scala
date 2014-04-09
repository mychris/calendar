package util.concurrent

import scala.concurrent._

/**
  *
  * @author Simon Kaltenbacher
  */
/*object FutureRight {

  def futureRight[A, B](body: => RightProjection[A, B]):FutureRight[A, B] = new FutureRight(future { body })
}

/**
  *
  * @author Simon Kaltenbacher
  */
class FutureRight[A, B](futureRight: Future[RightProjection[A, B]]) {

  /** */
  def flatMap[C](f: B => FutureRight[A, C]): FutureRight[A, C] =
    futureRight.flatMap {
      case Right(value) => f(value)
      case left         => future { left }
    }

  /** */
  def map[C](f: B => C): FutureRight[A, C] = futureRight.map(f)

  /** */
  def withFilter(p: A => Boolean) 
}*/