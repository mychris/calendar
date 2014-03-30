package service

import akka.actor._

import datasource.user._

import scala.slick.driver.PostgresDriver.simple._

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

/**
  *
  * @author Simon Kaltenbacher
  */
object UserService {

  def props(db: Database): Props = Props(classOf[UserService], db)
}

/**
  *
  * @author Simon Kaltenbacher
  */
class UserService(db: Database)
  extends Actor with
          ActorLogging with
          UserDataAccessComponentImpl {

  /** */
  protected object userDataAccess extends UserDataAccessModuleImpl

  import userDataAccess._

  /** */
  def getUserById(id: Int) =
    db.withSession { implicit session =>
      sender ! usersById(id).firstOption.fold[Any](NoSuchUserError(s"User with id $id does not exist!"))(UserById(_))
    }

  /** */
  def getUserByName(name: String) =
    db.withSession { implicit session =>
      sender ! usersByName(name).firstOption.fold[Any](NoSuchUserError(s"User with name $name does not exist!"))(UserByName(_))
    }

  def receive =  {
    case GetUserById(id)     => getUserById(id)
    case GetUserByName(name) => getUserByName(name)
  }
}