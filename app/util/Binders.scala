package util

import hirondelle.date4j.DateTime

import java.util.TimeZone

import play.api.mvc.QueryStringBindable
import play.api.mvc.QueryStringBindable._

object Binders {

  val timeZone = TimeZone.getTimeZone("UTC")

  implicit def bindableSeq[A](implicit bindableA: QueryStringBindable[A]) = new QueryStringBindable[Seq[A]] {

    def bind(key: String, params: Map[String, Seq[String]]) = {

      def bindSeq(values: Seq[String]): Seq[A] =
        values.flatMap(a => bindableA.bind(a.trim, params)).flatMap(_.right.toOption)

      Some(Right(bindSeq(key.split(",").toSeq)))
    }

    def unbind(key: String, value: Seq[A]) = value.map(bindableA.unbind(key, _)).mkString(",")
  }

  implicit object bindableDateTime extends Parsing[DateTime](
    str => DateTime.forInstant(str.toLong, timeZone),
    _.getMilliseconds(timeZone).toString,
    (key: String, e: Exception) => "Cannot parse parameter %s as DateTime: %s".format(key, e.getMessage)
  )
}
