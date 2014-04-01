package service.protocol

/*
 * Requests
 */

/** */
case object GetDdl extends Request

/*
 * Responses
 */

/** */
case class Ddl(ddl: scala.slick.driver.PostgresDriver.SchemaDescription) extends Response