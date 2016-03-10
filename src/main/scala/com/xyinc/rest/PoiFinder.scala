package com.xyinc.rest

import akka.actor.Actor

import com.xyinc.http.routes._

class PoiFinderActor extends Actor with PoiServiceRoute {
  def actorRefFactory = context
  def receive = runRoute(poisRoute)
}

