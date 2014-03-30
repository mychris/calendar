package controllers

import play.api.mvc._
import service._

/**
  *
  * @author Simon Kaltenbacher
  */
trait ResponseHandling {

	/** */
	implicit def response2ErrorHandler(response: Response) = new ErrorHandler(response)

	/** */
	class ErrorHandler(response: Response) {

		def handleError(f: Response => Result): Result = response match {
			case DatabaseConnectionError(_) => Results.ServiceUnavailable
			case _												  => f(response)
		}
	}
}