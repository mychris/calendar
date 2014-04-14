package controllers

import akka.pattern.AskTimeoutException

import formatters._

import play.api.libs.json.{util => _, _}
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{util => _, _}

import service._
import service.protocol._

import util.JsonConversion._
import java.util.concurrent.TimeoutException

/**
 *
 * @author Simon Kaltenbacher
 * @author Florian Liebhart
 */
trait ResponseSerialization {

  self: ExecutionEnvironment =>

  /**
   * TODO: Carefully observe exceptions sent to the client, and catch them here to handle them better! */
  def toJsonResult[W](block: => Future[W])(implicit ws: Writes[W]) =
    block
      .map(result => Ok(result.toJson))
      .recover {
        case NoSuchUserError(message)        => NotFound(message.toJson)
        case NoSuchTagError(message)         => NotFound(message.toJson)
        case NoSuchAppointmentError(message) => NotFound(message.toJson)
        case DatabaseError(message)          => InternalServerError(message.toJson)
        case e: AskTimeoutException          => InternalServerError(e.getMessage)
        case e: Exception                    => InternalServerError(e.getMessage)
      }
}