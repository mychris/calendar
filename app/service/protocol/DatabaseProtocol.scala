package service.protocol

/*
 * Errors
 */

/** */
case class SchemaChangeError(message: String) extends Error(message)

/** */
case class DatabaseConnectionError(message: String) extends Error(message)
