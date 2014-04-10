package datasource.user

trait AbstractUser {

  def id       : Int
  def name     : String
  def password : String
}

case class User(id: Int, val name: String, val password: String) extends AbstractUser
