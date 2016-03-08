package com.xyinc.dto

import spray.json._
import DefaultJsonProtocol._

/**
 * Points of interest (Poi) entity.
 *
 * @param id    unique identify poi
 * @param name  name of the poi
 * @param x     coordinate in x
 * @param y     coordinate in y
 */
case class Poi(
  id:        Int,
  name:      String,
  x:         Int,
  y:         Int
)

/**
 * Provide JsonFormat for Poi
 */
object PoiJsonProtocol extends DefaultJsonProtocol {
  implicit val poiFormat = jsonFormat4(Poi.apply)
}
