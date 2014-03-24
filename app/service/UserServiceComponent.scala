package app.service

import akka.actor._

import app.datasource.UserDataAccessComponent

import scala.slick.driver.PostgresDriver.simple._

/**
  *
  * @author Simon Kaltenbacher
  */
trait UserServiceComponent {

  self: UserDataAccessComponent =>

  /** Database connection */
  val db: Database

  /** User data access module accessor */
  val userDataAccess: UserDataAccessModule

  import userDataAccess._

  trait UserServiceModule {

    /*
     * Requests
     */

    /** */
    case class GetUserByName(name: String)

    /*
     * Answers
     */

    /** */
    case class UserByName(user: User)

    /*
     * Errors
     */

    /** */
    case class NoSuchUserError(message: String) extends Error

    /** */
    object UserService {

      def props: Props = Props(classOf[UserService])
    }

    /** */
    class UserService extends Actor with ActorLogging {

      /** */
      def getUserByName(name: String) =
        db.withSession { implicit session =>
          sender ! userDataAccess.usersByName(name).firstOption.fold[Any](NoSuchUserError(s"User with name $name does not exist!"))(UserByName(_))
        }

      def receive =  {
        case GetUserByName(name) => getUserByName(name)
      }
    }
  }
}