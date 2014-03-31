package service

import akka.util.Timeout

import scala.concurrent.duration._

import play.api.libs.concurrent.Akka
import play.api.Play.current

/**
  *
  * @author Simon Kaltenbacher
  */
trait ExecutionEnvironment {

	/** */
	val system = Akka.system

  /** */
  implicit val timeout = Timeout(10 seconds)
  
  /** */
  implicit val dispatcher = system.dispatcher
}