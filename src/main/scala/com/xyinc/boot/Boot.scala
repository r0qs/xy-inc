package com.xyinc.boot

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import scala.concurrent.duration._
import scala.util.{Failure, Success}
import org.slf4j.LoggerFactory

import com.xyinc.config.ConfigDatabaseSupport
import com.xyinc.rest.PoiFinderActor
import com.xyinc.dal.DAL
import com.xyinc.rest.Api

object Boot extends App with ConfigDatabaseSupport with DAL with Api {
  private val log = LoggerFactory.getLogger(getClass)

  implicit val system = ActorSystem("XY-incPOIFinderSystem")
  implicit val executionContext = system.dispatcher
  implicit val timeout = Timeout(5.seconds)

  // Service actor that replies to incoming HttpRequests
  val service = system.actorOf(PoiFinderActor.props(routes))
  val interface = system.settings.config.getString("app.interface")
  val port = system.settings.config.getInt("app.port")
 
  /**
   * Initialize database
   */
  createDBSchema() onComplete {
    case Success(_) => log.info("Database schema successfully created")
    case Failure(ex) => log.error("Database create failed - {}", ex.getMessage)
  }

  val server = (IO(Http) ? Http.Bind(service, interface, port))
    .mapTo[Http.Event]
    .map {
      case Http.Bound(address) =>
        log.info(s"PoiFinder Service bound to $address")
      case Http.CommandFailed(cmd) =>
        log.error("PoiFinder Service could not bind to " +
          s"$interface:$port, ${cmd.failureMessage}")
        system.shutdown()
    }
}
