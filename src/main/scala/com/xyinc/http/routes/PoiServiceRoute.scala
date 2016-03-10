package com.xyinc.http.routes

import akka.event.slf4j.SLF4JLogging
import spray.routing._
import spray.http._
import spray.json._
import scala.util.{ Success, Failure }
import spray.httpx.marshalling.ToResponseMarshallable

import com.xyinc.services._
import com.xyinc.dto.Poi
import com.xyinc.dto.PoiJsonProtocol._

trait PoiServiceRoute extends HttpService with PoiService with SLF4JLogging {
  private implicit def ec = actorRefFactory.dispatcher

  val poisRoute = {
    (get & path("")) {
      complete {
        "TODO: put routes preview here"
      }
    } ~
    // Get all pois
    (get & path("pois" / "all")) {
      complete(getAllPois())
    } ~
    // Create a single Poi
    (post & path("pois")) {
      entity(as[Poi]) { poi =>
        log.debug("Creating POI: %s".format(poi))
        val insertedPoi = insertPoi(poi)
        complete(StatusCodes.Created, insertedPoi)
      }
    } ~
    // Get a single Poi by id
    (get & path("pois" / IntNumber)) { id =>
      log.debug("Retrieving POI with id %d".format(id))
      complete(getPoi(id))
    } ~
    // Search for POIs with distance less than or equals dmax from a given coordinate (x,y)
    (get & path("pois" / "nearest")) {
      parameters("x".as[Int], "y".as[Int], "dmax".as[Int]) { (x, y, dmax) =>
        log.debug("Searching for POIs near to: (%s, %s) up to: %s".format(x, y, dmax))
        complete(searchNearestPois(x, y, dmax))
      }
    } ~
    // Delete a single Poi
    (delete & path("pois" / IntNumber)) { id =>
      log.debug("Deleting POI with id %d".format(id))
      import DefaultJsonProtocol._
      onComplete(deletePoi(id)) {
        case Success(value) => complete(value)
        case Failure(ex)    => complete(ex.getMessage)
      }
    }
  }
}
