package com.xyinc.service

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._

class POIFinderActor extends Actor with POIFinder {
  def actorRefFactory = context
  def receive = runRoute(defaultRoute)
}

// this trait defines the service behavior independently from the service actor
trait POIFinder extends HttpService {

  val defaultRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) {
          complete {
            <html>
              <body>
                <h1>up!</h1>
              </body>
            </html>
          }
        }
      }
    }
}
