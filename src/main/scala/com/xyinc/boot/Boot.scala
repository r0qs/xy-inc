package com.xyinc.boot

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import spray.can.Http

import scala.concurrent.duration._

import com.xyinc.service.POIFinderActor

object Boot extends App {

  implicit val system = ActorSystem("XY-incPOIFinderSystem")

  // the service actor that replies to incoming HttpRequests
  val service = system.actorOf(Props[POIFinderActor], name = "poifinder-service")
  val interface = system.settings.config.getString("app.interface")
  val port = system.settings.config.getInt("app.port")
  
  implicit val timeout = Timeout(5.seconds)
  val server = IO(Http) ? Http.Bind(service, interface, port)
}
