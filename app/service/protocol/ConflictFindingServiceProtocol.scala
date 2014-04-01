package service.protocol

import datasource.calendar._

/*
 * Requests
 */

/** */
case class FindConflict(appointments: List[Appointment]) extends Request

/*
 * Reponses
 */

/** */
case class Conflicts(conflicts: List[(Appointment, Appointment)]) extends Response