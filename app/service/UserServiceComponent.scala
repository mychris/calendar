package app.service

import akka.actor._

import app.datasource._

import scala.slick.driver.PostgresDriver.simple._

/**
  *
  * @author Simon Kaltenbacher
  */
trait UserServiceComponent {

  self: UserDataAccessComponentImpl =>

  /** Database */
  val db: Database

  /** User data access module accessor */
  val userDataAccess: UserDataAccessModuleImpl

  import userDataAccess._

  trait UserServiceModule {

    /** */
    object protocol {

      /*
       * Requests
       */

      /** */
      case class GetUserByName(name: String) extends Request

      /*
       * Reponses
       */

      /** */
      case class UserByName(user: User) extends Response

      /*
       * Errors
       */

      /** */
      case class NoSuchUserError(message: String) extends Error
    }

    /** */
    trait PropsFactory {

      def userService: Props
    }

    /** */
    val factory: PropsFactory

    /** */
    trait UserService extends Actor
  }
}

/**
  *
  * @author Simon Kaltenbacher
  */
trait UserServiceComponentImpl extends UserServiceComponent {

  self: UserDataAccessComponentImpl =>

  import userDataAccess._

  trait UserServiceModuleImpl extends UserServiceModule {

    import protocol._

    /** */
    object factory extends PropsFactory {

      def userService = Props(classOf[UserServiceImpl])
    }

    /** */
    class UserServiceImpl extends UserService with Actor with ActorLogging {

      /** */
      def getUserByName(name: String) =
        db.withSession { implicit session =>
          sender ! usersByName(name).firstOption.fold[Any](NoSuchUserError(s"User with name $name does not exist!"))(UserByName(_))
        }

      def receive =  {
        case GetUserByName(name) => getUserByName(name)
      }
    }
  }
}