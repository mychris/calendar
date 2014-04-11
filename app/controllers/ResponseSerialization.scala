package controllers

import play.api.libs.json.{util => _, _}
import play.api.mvc._
import play.api.mvc.Results._

import scala.concurrent.{util => _, _}

import formatters._
import service._
import service.protocol._
import util.json._

/**
  *
  * @author Simon Kaltenbacher
  */
trait ResponseSerialization {

  self: ExecutionEnvironment =>

  /** */
  def toJsonResult[W](body: => Future[W])(implicit w: Writes[W]) =
    body
      .map(result => Ok(result.toJson))
      .recover {
        case NoSuchUserError(message)         => NotFound(message.toJson)
        case NoSuchTagError(message)          => NotFound(message.toJson)
        case NoSuchAppointmentError(message)  => NotFound(message.toJson)
        case DatabaseConnectionError(message) => InternalServerError("Database connection lost!")
        case e: Exception                     => InternalServerError(e.toJson)
      }
}