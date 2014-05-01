package datasource.calendar

import scala.slick.driver.PostgresDriver.simple.{Tag => _, _}

import com.github.nscala_time.time.Imports._

import util._

trait AbstractAppointment {

  def id    : Int
  def title : String
  def start : DateTime
  def end   : DateTime
}    

trait AbstractTag {

  def id       : Int
  def name     : String
  def priority : Int
  def color    : Color
  def userId   : Int
} 

case class Appointment(id: Int, title: String, start: DateTime, end: DateTime) extends AbstractAppointment
case class Tag(id: Int, name: String, priority: Int, color: Color, userId: Int) extends AbstractTag