package com.xyinc.http.routes

import akka.event.slf4j.SLF4JLogging
import akka.actor.ActorRefFactory
import spray.routing._
import spray.http._
import spray.http.StatusCodes
import spray.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{ Success, Failure }
import scala.concurrent.Future

import com.xyinc.dto.Poi
import com.xyinc.dal.DAL

class PoiServiceRoute(val dataAccess: DAL) (implicit val actorRefFactory: ActorRefFactory) 
  extends HttpService with SLF4JLogging {

  import com.xyinc.dto.PoiJsonProtocol._
  import spray.httpx.SprayJsonSupport._
  import dataAccess._

  // Rejections
  // TODO: put this in a trait
  val negativeParametersErrorMsg = "ERROR: parameters must be a positive number!"

  implicit val rejectionHandler = RejectionHandler {
    case ValidationRejection(rejectionError, None) :: _ =>
      complete(StatusCodes.BadRequest, rejectionError)
  }

  val routes = {
    (get & path("")) {
      complete("")
    } ~
    // Get all pois
    (get & path("pois" / "all")) {
        onComplete(getAllPois()) {
          case Success(list) => complete(list)
          case Failure(ex) => failWith(ex)
        }
    } ~
    // Create a single Poi
    (post & path("pois")) {
      entity(as[Poi]) { poi =>
        validate((poi.x >=0 && poi.y >= 0), negativeParametersErrorMsg) {
          log.debug("Creating POI: %s".format(poi))
          val insertedPoi = insertPoi(poi)
          complete(StatusCodes.Created, insertedPoi)
        }
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
        validate((x >=0 && y >= 0 && dmax >= 0), negativeParametersErrorMsg) {
          log.debug("Searching for POIs near to: (%s, %s) up to: %s".format(x, y, dmax))
          complete(searchNearestPois(x, y, dmax))
        }
      }
    } ~
    // Delete a single Poi
    (delete & path("pois" / IntNumber)) { id =>
      log.debug("Deleting POI with id %d".format(id))
      import DefaultJsonProtocol._
      complete(deletePoi(id))
    }
  }
}
