package datasource.appointmentproposal

import scala.slick.driver.PostgresDriver.simple.{Tag => _, _}

import hirondelle.date4j.DateTime

trait AbstractAppointmentProposal {

  def id          : Int
  def description : String
  def creatorId   : Int
}

trait AbstractAppointmentProposalTime {

  def id         : Int
  def start      : DateTime
  def end        : DateTime
  def proposalId : Int
}

case class AppointmentProposal(id: Int, description: String, creatorId: Int) extends AbstractAppointmentProposal
case class AppointmentProposalTime(id: Int, start: DateTime, end: DateTime, proposalId: Int) extends AbstractAppointmentProposalTime

object Vote extends Enumeration {

  type Vote = Value

  val NotVoted  = Value(0)
  val Accepted  = Value(1)
  val Refused   = Value(2)
  val Uncertain = Value(3)
}