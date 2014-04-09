package controllers

import formatters._

import play.api.libs.json.{util => _, _}
import play.api.mvc._
import play.api.mvc.Results._

import service.protocol._

import util.json._

/**
  *
  * @author Simon Kaltenbacher
  */
trait ResponseTransformation {

  /** */
  implicit def response2ResponseTransformer(response: Response) = new ResponseTransformer(response)

  /** */
  def errorTransformer(error: Error) = error match {
    case NoSuchUserError(_)
       | NoSuchTagError(_)
       | NoSuchAppointmentError(_) => NotFound(error.toJson)
    case BadFormatError(_)         => BadRequest(error.toJson)
    case _                         => InternalServerError(error.toJson)
  }

  /** */
  def successTransformer(success: Success) = Ok(success.toJson)

  /** */
  class ResponseTransformer(response: Response) {

    def toJsonResult: SimpleResult = response.fold(errorTransformer _, successTransformer _)
  }
}