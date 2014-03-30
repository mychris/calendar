package datasource.user

import scala.slick.driver.PostgresDriver.simple._

/** */
trait AbstractUser {

  def id       : Int
  def name     : String
  def password : String
}

/** */
case class User(id: Int, val name: String, val password: String) extends AbstractUser
