package models

case class User(name: String, password: String)

object User {
  val users = Seq(User("florian","ftest"), User("chris", "ctest"), User("simon", "stest"))

  def exists(name: String, password: String) = users.exists(u => (u.name equals name) && (u.password equals password))
}