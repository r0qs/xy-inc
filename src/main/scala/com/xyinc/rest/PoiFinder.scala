package com.xyinc.rest

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.Logging._
import spray.http.{HttpRequest, HttpResponse}
import spray.routing._
import spray.routing.directives.LogEntry

import com.xyinc.http.routes._
import com.xyinc.dal.DAL
import com.xyinc.config.DatabaseSupport

object PoiFinderActor {
  def props(routes: Route) = Props.create(classOf[PoiFinderActor], routes)
}

class PoiFinderActor(routes: Route) extends HttpService with Actor with ActorLogging {
  def actorRefFactory = context

  def receive = runRoute(
    logRequestResponse(showRequestResponses _)(routes)
  )

  /**
   * Log each request and response.
   */
  def showRequestResponses(request: HttpRequest): Any => Option[LogEntry] = {
    case HttpResponse(status, _, _, _) => Some(LogEntry(s"${request.method} ${request.uri} ($status)", InfoLevel))
    case response => Some(LogEntry(s"${request.method} ${request.uri} $response", WarningLevel))
  }
}

