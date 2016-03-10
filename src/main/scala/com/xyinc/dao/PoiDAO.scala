package com.xyinc.dao

import slick.driver.H2Driver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.meta.MTable

import com.xyinc.dto._
import com.xyinc.database._
import com.xyinc.database.PoisTable
import com.xyinc.config.AppConfig

/**
 * API for managing 'Pois' in the database
 * Creates the SQLite database and define operations
 */
class PoiDAO {

  lazy val pois = PoisTable
  
  // Init Database instance
  val db = Database.forConfig("database")

  // Create table action
  val setupAction: DBIO[Unit] = DBIO.seq(pois.schema.create)

  // Create tables if not exist
  val createTableIfNotExists = db.run(DBIO.seq(
      MTable.getTables map (tables => {
        if (!tables.exists(_.name.name == pois.baseTableRow.tableName))
          db.run(setupAction)
      })
  )) 
  
  // Drop db table
  def clear = db.run(DBIO.seq(pois.schema.drop))

  /**
   * Get all Pois from the database
   * @return A Seq of all Pois currently in the database
   */
  def getAll(): Future[Seq[Poi]] = db.run(pois.result)

  /**
   * Retrieves specific Poi from database.
   *
   * @param id id of the Poi to retrieve
   * @return Option[Poi] entity with specified id or None if not found
   */
  def get(id: Int): Future[Option[Poi]] = db.run(pois.findById(id)).map(_.headOption)

  /**
   * Saves Poi entity into database.
   *
   * @param poi Poi entity to store
   * @return the saved Poi entity
   */
  def insert(poi: Poi) : Future[Poi] = db.run(pois.create(Poi(0, poi.name, poi.x, poi.y)))

  /**
   * Deletes Poi from database.
   *
   * @param id id of the Pois to delete
   * @return 1 if Poi was deleted or 0 if not
   */
  def delete(id: Int): Future[Int] = db.run(pois.deleteById(id))

  /**
   * Retrieves a Seq of Pois with distance less than or equals dmax 
   * from a given coordinate (x,y) from database.
   *
   * @param x 
   * @param y
   * @param dmax
   * @return Seq of Pois that are nearest from the point(x,y) based on the dmax
   */
  def searchNearest(x: Int, y: Int, dmax: Int): Future[Seq[Poi]] = 
    db.run(pois.nearestNeighborSearch(x, y, dmax))
}

