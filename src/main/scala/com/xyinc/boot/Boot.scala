package com.xyinc.boot

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout

import spray.can.Http

import scala.concurrent.duration._

import com.xyinc.rest.POIFinderActor

object Boot extends App {

  implicit val system = ActorSystem("XY-incPOIFinderSystem")
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5.seconds)

  // Service actor that replies to incoming HttpRequests
  val service = system.actorOf(Props[POIFinderActor], name = "poifinder-service")
  val interface = system.settings.config.getString("app.interface")
  val port = system.settings.config.getInt("app.port")
  
  val server = (IO(Http) ? Http.Bind(service, interface, port))
    .mapTo[Http.Event]
    .map {
      case Http.Bound(address) =>
        println(s"PoiFinder Service bound to $address")
      case Http.CommandFailed(cmd) =>
        println("PoiFinder Service could not bind to " +
          s"$interface:$port, ${cmd.failureMessage}")
        system.shutdown()
    }
}
