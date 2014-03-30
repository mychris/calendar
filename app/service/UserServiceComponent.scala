package service

import akka.actor._

import datasource._

import scala.slick.driver.PostgresDriver.simple._

import service.baseprotocol._

/**
  *
  * @author Simon Kaltenbacher
  */
trait UserServiceComponent {

  self: UserDataAccessComponentImpl =>

  /** Database */
  protected val db: Database

  /** User data access module accessor */
  protected val userDataAccessImpl: UserDataAccessModuleImpl

  import userDataAccessImpl._

  trait UserServiceModule {

    /** */
    object protocol {

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

  import userDataAccessImpl._

  trait UserServiceModuleImpl extends UserServiceModule {

    import protocol._

    /** */
    object factory extends PropsFactory {

      def userService = Props[UserServiceImpl]
    }

    /** */
    class UserServiceImpl extends UserService with Actor with ActorLogging {

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
  }
}