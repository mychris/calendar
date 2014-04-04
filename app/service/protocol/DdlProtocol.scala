package service.protocol

import scala.slick.driver.PostgresDriver.SchemaDescription

/*
 * Requests
 */

/** */
case object GetDdl extends Request

/*
 * Responses
 */

/** */
case class Ddl(ddl: SchemaDescription) extends Success