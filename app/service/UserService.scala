package service

import akka.actor._

import datasource.user._

import scala.slick.driver.PostgresDriver.simple._

import service.protocol._

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
class UserService(db: Database, calenderService: ActorRef)
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

  /** */
  def addUser(name: String, password: String) = db.withSession { implicit session =>
    sender ! UserAdded((users returning users.map(_.id)) += User(-1, name, password)) 
  }

  /** */
  def getDdl = sender ! Ddl(userDdl)

  def receive =  {
    case GetUserById(id)     => getUserById(id)
    case GetUserByName(name) => getUserByName(name)
    case AddUser(name, pw)   => addUser(name, pw)
    case GetDdl              => getDdl
  }
}