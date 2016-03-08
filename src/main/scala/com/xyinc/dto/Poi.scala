package com.xyinc.dto

import spray.json._
import DefaultJsonProtocol._
import slick.jdbc.GetResult

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

object Poi {
  /**
   * Result set getters
   * Dirty hack from: http://slick.typesafe.com/doc/3.1.1/sql.html#result-sets
   */
  implicit val getPoiResult = GetResult(r => Poi(r.<<, r.<<, r.<<, r.<<))
}

/**
 * Provide JsonFormat for Poi
 */
object PoiJsonProtocol extends DefaultJsonProtocol {
  implicit val poiFormat = jsonFormat4(Poi.apply)
}
