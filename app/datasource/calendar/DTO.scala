package datasource.calendar

import util.slick._

import hirondelle.date4j.DateTime

import scala.slick.driver.PostgresDriver.simple.{Tag => _, _}
import scala.slick.lifted.Constraint

/** */
trait AbstractAppointment {

  def id          : Int
  def description : String
  def start       : DateTime
  def end         : DateTime
}    

/** */
trait AbstractTag {

  def id       : Int
  def name     : String
  def priority : Int
  def userId   : Int
} 

/** */
case class Appointment(id: Int, description: String, start: DateTime, end: DateTime) extends AbstractAppointment

/** */
case class Tag(id: Int, name: String, priority: Int, userId: Int) extends AbstractTag