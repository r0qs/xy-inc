package com.xyinc.dao

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import com.xyinc.dto._
import com.xyinc.database._
import com.xyinc.database.PoisTable
import com.xyinc.config.DatabaseSupport
import com.xyinc.config.Driver
/**
 * API for managing 'Pois' in the database
 * Creates the SQLite database and define operations
 */
trait PoiDAO extends PoisTable with DatabaseSupport {
  this: Driver =>
  import driver.api._

  /**
   * Get all Pois from the database
   * @return A List of all Pois currently in the database
   */
  def getAllPois(): Future[List[Poi]] = db.run(pois.result).map(_.toList)

  /**
   * Retrieves specific Poi from database.
   *
   * @param id id of the Poi to retrieve
   * @return Option[Poi] entity with specified id or None if not found
   */
  def getPoi(id: Int) = db.run(pois.findById(id)).map(_.headOption)

  /**
   * Saves Poi entity into database.
   *
   * @param poi Poi entity to store
   * @return the saved Poi entity
   */
  def insertPoi(poi: Poi) : Future[Poi] = db.run(pois.create(Poi(0, poi.name, poi.x, poi.y)))

  /**
   * Deletes Poi from database.
   *
   * @param id id of the Pois to delete
   * @return "1" if Poi was deleted or "0" if not
   */
  def deletePoi(id: Int) = db.run(pois.deleteById(id)).map(_.toString)

  /**
   * Retrieves a Seq of Pois with distance less than or equals dmax 
   * from a given coordinate (x,y) from database.
   *
   * @param x
   * @param y
   * @param dmax
   * @return Seq of Pois that are nearest from the point(x,y) based on the dmax
   */
  def searchNearestPois(x: Int, y: Int, dmax: Int) =
    db.run(pois.nearestNeighborSearch(x, y, dmax)).map(_.to[List])
}

