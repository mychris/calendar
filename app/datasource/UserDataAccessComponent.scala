package app.datasource

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.lifted.Shape

/**
  *
  * @author Simon Kaltenbacher
  */
trait UserDataAccessComponent {

  /** */
  trait UserDataAccessModule {

    /*
     * Types
     */

    /** User data transfer object representation */
    type User <: AbstractUser

    /** User database table representation */
    type UserTable <: AbstractUserTable

    /*
     * Shapes
     */
 
    /** */
    implicit val userShape: Shape[_, UserTable, User, UserTable]
 
    /*
     * Data Transfer Objects
     */
 
    /** */
    trait AbstractUser {
 
      def id       : Int
      def name     : String
      def password : String
    }

    /*
     * Database tables
     */

    /** */
    trait AbstractUserTable extends Table[User] {

      def id       : Column[Int]
      def name     : Column[String]
      def password : Column[String]
    }

    /*
     * Queries
     */

    /** */
    val users: TableQuery[UserTable]

    /** */
    def usersById(id: Int): Query[UserTable, User] = users.filter(_.id === id)

    /** */
    def usersByName(name: String): Query[UserTable, User] = users.filter(_.name === name)
  }
}

/**
  *
  * @author Simon Kaltenbacher
  */
trait UserDataAccessComponentImpl extends UserDataAccessComponent {

  trait UserDataAccessModuleImpl extends UserDataAccessModule {

    /*
     * Types
     */

    type User = UserImpl

    type UserTable = UserTableImpl

    /*
     * Shapes
     */

    implicit val userShape: Shape[_, UserTable, User, UserTable] = implicitly[Shape[_, UserTable, User, UserTable]]

    /*
     * Data transfer objects
     */

    protected case class UserImpl(id: Int, val name: String, val password: String) extends AbstractUser

    /*
     * Database tables
     */

    protected class UserTableImpl(tag: Tag) extends Table[User](tag, "app_user") with AbstractUserTable {

      def id       = column[Int   ]("id"      , O.PrimaryKey, O.AutoInc)
      def name     = column[String]("name"    , O.NotNull              )
      def password = column[String]("password", O.NotNull              )

      def *        = (id, name, password) <> (UserImpl.tupled, UserImpl.unapply)
    }

    /*
     * Queries
     */

    val users = TableQuery[UserTable]
  }
}