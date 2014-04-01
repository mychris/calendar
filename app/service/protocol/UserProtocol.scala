package service.protocol

import datasource.user._

/*
 * Requests
 */

/** */
case class GetUserById(id: Int) extends Request

/** */
case class GetUserByName(name: String) extends Request

/*
 * Reponses
 */

/** */
case class UserById(user: User) extends Response

/** */
case class UserByName(user: User) extends Response

/*
 * Errors
 */

/** */
case class NoSuchUserError(message: String) extends Error