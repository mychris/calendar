package service.protocol

import datasource.user._

case class UserWithoutPassword(id: Int, name: String)

/*
 * Requests
 */
case class GetUserById(id: Int) extends Request
case class GetUserByName(name: String) extends Request
case class GetAllUsers() extends Request
case class AddUser(name: String, password: String) extends Request

/*
 * Reponses
 */
case class UserById(user: User) extends Success
case class UserByName(user: User) extends Success
case class AllUsers(users: Seq[UserWithoutPassword]) extends Success
case class UserAdded(id: Int) extends Success

/*
 * Errors
 */
case class NoSuchUserError(message: String) extends Error(message)
case class DuplicateUsername(message: String) extends Error(message)