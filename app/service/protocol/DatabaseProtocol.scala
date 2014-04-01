package service.protocol

/*
 * Errors
 */

/** */
case class SchemaChangeError(message: String) extends Error

/** */
case class DatabaseConnectionError(message: String) extends Error
