package com.xyinc.database

import slick.driver.SQLiteDriver.api._

import com.xyinc.dto.Poi

/**
 * Defines the POIS table.
 */
class PoisTable(tag: Tag) extends Table[Poi](tag, "POIS") {
  //FIXME: is better use id as a Option[Int]?
  def id      = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name    = column[String]("NAME")
  def x       = column[Int]("X_COORD")
  def y       = column[Int]("Y_COORD")
  def *       = (id, name, x, y) <> (Poi.tupled, Poi.unapply)
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
}
