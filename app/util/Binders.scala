package util

import com.github.nscala_time.time.Imports._

import play.api.mvc.QueryStringBindable
import play.api.mvc.QueryStringBindable._

object Binders {

  implicit object bindableDateTime extends Parsing[DateTime](
    str => new DateTime(str.toLong),
    _.getMillis.toString,
    (key: String, e: Exception) => "Cannot parse parameter %s as DateTime: %s".format(key, e.getMessage)
  )
}
