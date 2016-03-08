package com.xyinc.dao

import slick.driver.SQLiteDriver.api._
// FIXME: use implicit execution context for futures
//import system.dispatcher
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.meta.MTable
//FIXME: the following imports will be removed!
import scala.concurrent.Await
import scala.concurrent.duration._

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
  private val db = Database.forURL(AppConfig.DB.url ,driver = AppConfig.DB.driver)

  // Create table action
  val setupAction: DBIO[Unit] = DBIO.seq(
    pois.schema.create,
    pois += Poi(0, "Lanchonete", 27, 12)
  )

  // Create tables if not exist
  val createTableIfNotExists = db.run(DBIO.seq(
      MTable.getTables map (tables => {
        if (!tables.exists(_.name.name == pois.baseTableRow.tableName))
          db.run(setupAction)
      })
  )) 

  def insert(name: String, x: Int, y: Int) : Future[Poi] = {
    val action = pois.create(Poi(0, name, x, y))
    db.run(action)
  }

  def get(id: Int): Future[Option[Poi]] = {
    val action = pois.findById(id).map(_.headOption)
    db.run(action)
  }

  def delete(id: Int): Future[Int] = {
    val action = pois.deleteById(id)
    db.run(action)
  }

  /**
   * Get all Pois from the database
   * @return A List of all Pois currently in the database
   */
  def getAllPois(): List[Poi] = {
    // select * from POIS;
    val action = pois.to[List].result
    val result: Future[List[Poi]] = db.run(action)
    //FIXME: Don't block here
    // Pass a future and resolve it in PoiFinder
    val res = Await.result(result, 1 second)
    println(res)
    res
  }

  /**
   * Retrieves specific Poi from database.
   *
   * @param id id of the Poi to retrieve
   * @return Option[Poi] entity with specified id or None if not found
   */
  def getPoi(id: Int) = {
    val result: Future[Option[Poi]] = get(id)
    val res = Await.result(result, 1 second)
    println(res)
    res
  }

  /**
   * Saves Poi entity into database.
   *
   * @param poi Poi entity to store
   * @return the saved Poi entity
   */
  def insertPoi(poi: Poi): Poi = {
    //FIXME: x and y must be positive and name not a empty String
    val result: Future[Poi] = insert(poi.name, poi.x, poi.y)
    val res = Await.result(result, 1 second)
    println(res)
    res
  }

  /**
   * Deletes Poi from database.
   *
   * @param id id of the Pois to delete
   * @return 1 if Poi was deleted or 0 if not
   */
  def deletePoi(id: Int) = {
    val result: Future[Int] = delete(id)
    val res = Await.result(result, 1 second)
    println(res)
    res
  }
 
  /**
   * Retrieves list of Pois with distance less than or equals dmax 
   * from a given coordinate (x,y) from database.
   *
   * @param x 
   * @param y
   * @param dmax
   * @return list of Pois that are nearest from the point(x,y) based on the dmax
   */
  //TODO: implement this!
  def searchNearestPois(x: Int, y: Int, dmax: Int) = ???
}

