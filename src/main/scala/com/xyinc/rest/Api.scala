package com.xyinc.rest

import spray.routing.Directives
import scala.concurrent.ExecutionContext
import akka.actor.ActorSystem

import com.xyinc.dal.DAL
import com.xyinc.config.DatabaseSupport
import com.xyinc.http.routes.PoiServiceRoute

trait Api extends Directives { 
  this: DatabaseSupport with DAL =>
  
  implicit def system: ActorSystem
  
  val poiService = new PoiServiceRoute(this)

  val routes = poiService.routes
}
