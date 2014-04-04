package util

import play.api.libs.json._

package object json {

  /** */
  implicit def value2JsonConverter[V : Writes](value: V) = new JsonSerializer(value)

  /** */
  class JsonSerializer[V : Writes](value: V) {

    def toJson = Json.toJson(value)
  }
}