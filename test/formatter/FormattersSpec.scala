package formatter

import play.api.mvc._
import play.api.libs.json.{util => _, _}

import org.scalatest._

import datasource.calendar._

import formatters._

class ConflictFindingServiceSpec() extends WordSpecLike with Matchers {

  val tagAsObject = datasource.calendar.Tag(1, "something", 6, 1)
  val tagAsString = """{"id":1,"name":"something","priority":6,"userId":1}"""

	"A Tag Formatter" must {

    "be able to converte a valid json tag to a tag object" in {
      assert(Json.parse(tagAsString).as[datasource.calendar.Tag] === tagAsObject)
    }

    "be able to converte the case class to a json string" in {
      assert(tagAsObject.toJson.toString === tagAsString)
    }

    "be able to converte back and forth" in {
      assert(Json.parse(tagAsObject.toJson.toString).as[datasource.calendar.Tag] === tagAsObject)
    }

    "be able to parse from AnyContentAsText" in {
      assert(AnyContentAsJson(Json.parse(tagAsString)).asJson.map(_.as[datasource.calendar.Tag]) === Some(tagAsObject))
    }

	}

}
