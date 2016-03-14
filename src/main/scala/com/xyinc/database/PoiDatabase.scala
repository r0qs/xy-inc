package com.xyinc.database

import com.xyinc.config.Driver
import com.xyinc.dto.Poi

trait PoisTable {
  this: Driver =>
  import driver.api._

  // Tables
  lazy val pois = PoisTable

  /**
   * Defines the POIS table.
   */
  class PoisTable(tag: Tag) extends Table[Poi](tag, "POIS") {
    def id      = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name    = column[String]("NAME")
    def x       = column[Int]("X_COORD")
    def y       = column[Int]("Y_COORD")
    def *       = (id, name, x, y) <> ((Poi.apply _).tupled, Poi.unapply)
  }

  // Define db actions of PoisTable
  object PoisTable extends TableQuery(new PoisTable(_)) {

    /**
     * Search for a specific Poi in db
     *
     * @param id the Poi id
     * @return the Poi entity if found or None
     */
    def findById(id: Int) = {
      this.filter(_.id === id).result
    }

    /**
     * Create and auto-increment Poi in db
     *
     * @param poi the Poi to insert in db
     * @return the Poi entity with auto-incremented id
     */
    def create(poi: Poi) = {
      (this returning this.map(_.id) into ((p, id) => p.copy(id = id)) += poi)
    }

    /**
     * Delete a specific Poi from db
     *
     * @param id the Poi id to be deleted
     * @return 1 if successful deleted or 0 if not
     */
    def deleteById(id: Int) = {
      this.filter(_.id === id).delete
    }

    /**
     * Find the nearest points of a given coordinate(x,y) until a maximum distance (dmax)
     *
     * @param x
     * @param y
     * @param dmax
     * @return list of Pois that are nearest from the point(x,y) based on the dmax (radius)
     *
     * FIXME: Use MongoDB or PostgreSQL to support R-trees data structures
     * used for efficient spatial access methods. So this is the
     * simplest way to solve the nearest neighbor search problem for this small test app.
     */
    def nearestNeighborSearch(x: Int, y: Int, dmax: Int) =
      sql"""SELECT * FROM POIS WHERE ((${x} - X_COORD) * (${x} - X_COORD) +
        (${y} - Y_COORD) * (${y} - Y_COORD)) <= (${dmax} * ${dmax})""".as[Poi]
  }
}
