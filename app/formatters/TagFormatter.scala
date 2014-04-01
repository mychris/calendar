package formatters

import play.api.libs.json.Json.toJson
import play.api.libs.json.JsValue
import play.api.libs.json.JsResult
import play.api.libs.json.JsSuccess
import play.api.libs.json.Format

import datasource.calendar.Tag

object TagFormatter {

  implicit object JsonTagFormatter extends Format[Tag] {

    def writes(o: Tag): JsValue = {
      toJson( Map(
        "id" -> toJson(o.id),
        "name" -> toJson(o.name),
        "priority" -> toJson(o.priority),
        "userId" -> toJson(o.userId)
      ))
    }

    def reads(json: JsValue): JsResult[Tag] = {
      JsSuccess(Tag(
        id = (json \ "id").as[Option[Int]].get,
        name = (json \ "name").as[Option[String]].get,
        priority = (json \ "priority").as[Option[Int]].get,
        userId = (json \ "userId").as[Option[Int]].get
      ))
    }
  }

}