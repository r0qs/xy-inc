package com.xyinc.rest

import akka.actor.{Actor, ActorLogging}
import akka.event.slf4j.SLF4JLogging
import spray.routing._
import spray.http._
import spray.json._
import spray.httpx.SprayJsonSupport._

import com.xyinc.dto.Poi
import com.xyinc.dto.PoiJsonProtocol._
import com.xyinc.dao.PoiDAO

class POIFinderActor extends Actor with POIFinder {
  def actorRefFactory = context
  def receive = runRoute(defaultRoute)
}

// This trait defines the service behavior independently from the service actor
trait POIFinder extends HttpService with SLF4JLogging {

  private val poiDAO = new PoiDAO 

  val defaultRoute = {
    (get & path("")) {
      complete {
        "TODO: put routes preview here"
      }
    } ~
    // Get all pois
    (get & path("pois" / "all")) {
      complete {
        poiDAO.getAllPois match { 
          case head :: tail => head :: tail
          case Nil => StatusCodes.NoContent
        }
      }
    } ~
    // Create a single Poi
    (post & path("pois")) {
      entity(as[Poi]) { poi => ctx =>
        log.debug("Creating POI: %s".format(poi))
        val insertedPoi = poiDAO.insertPoi(poi)
        ctx.complete(StatusCodes.Created, insertedPoi)
      }
    } ~
    // Get a single Poi by id
    (get & path("pois" / IntNumber)) { id =>
      log.debug("Retrieving POI with id %d".format(id))
      val poi = poiDAO.getPoi(id)
      complete(poi)
    } ~
    // Search for POIs with distance less than or equals dmax from a given coordinate (x,y)
    (get & path("pois" / "nearest")) {
      parameters("x".as[Int], "y".as[Int], "dmax".as[Int]) { (x, y, dmax) =>
        log.info("Searching for POIs with parameters: %s, %s, %s".format(x, y, dmax))
        complete(poiDAO.searchNearestPois(x, y, dmax))
      }
    } ~
    // Delete a single Poi
    (delete & path("pois" / IntNumber)) { id => ctx =>
      log.debug("Deleting POI with id %d".format(id))
      poiDAO.deletePoi(id)
      ctx.complete(StatusCodes.NoContent)
    }
  }
}

