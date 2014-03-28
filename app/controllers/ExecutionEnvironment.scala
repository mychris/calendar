package controllers

import akka.util.Timeout

import scala.concurrent.duration._

import service.Services

/**
  *
  * @author Simon Kaltenbacher
  */
trait ExecutionEnvironment {

  /** */
  implicit val timeout = Timeout(10 seconds)
  
  /** */
  implicit val dispatcher = Services.system.dispatcher
}