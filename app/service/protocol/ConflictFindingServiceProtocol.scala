package service.protocol

import datasource.calendar._

/*
 * Requests
 */

/** */
case class FindConflict(appointments: Seq[Appointment]) extends Request

/*
 * Reponses
 */

/** */
case class Conflicts(conflicts: Seq[(Appointment, Appointment)]) extends Success {

	def value = conflicts
}