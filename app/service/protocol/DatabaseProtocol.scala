package service.protocol

/*
 * Errors
 */

/** */
case class DatabaseError(message: String) extends Error(message)
