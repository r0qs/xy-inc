package com.xyinc.dao

import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import slick.jdbc.meta.MTable

import com.xyinc.dto._
import com.xyinc.dal.DAL
import com.xyinc.config.Driver
import com.xyinc.config.TestDatabaseSupport

class PoiDAOSuite
  extends FunSuite
  with BeforeAndAfter
  with ScalaFutures
  with TestDatabaseSupport
  with DAL {
  
  import driver.api._

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  val insertAction = Seq(          //IDs 
    Poi(0, "Lanchonete", 27, 12),  // 1
    Poi(0, "Posto", 31, 18),       // 2
    Poi(0, "Joalheria", 15, 12),   // 3
    Poi(0, "Floricultura", 19, 21),// 4
    Poi(0, "Pub", 12, 8),          // 5
    Poi(0, "Supermercado", 23, 6), // 6
    Poi(0, "Churrascaria", 28, 2)  // 7
  )

  def insertPois(): Int = db.run(pois ++= insertAction).futureValue.get

  before { 
    createDBSchema().futureValue
  }

  after {
    dropDBSchema().futureValue
  }

  test("Create database Schema") {
    val tables = db.run(MTable.getTables).futureValue
    assert(tables.size == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("POIS")) == 1)
  }

  test("Insert seven Pois on database") {
    val insertCount = insertPois()
    assert(insertCount == 7)
  }
  
  test("Query Pois from database") {
    insertPois()
    val results = db.run(pois.result).futureValue
    assert(results.size == 7)
    assert(results.head.id == 1)
  }
  
  test("Insert new Poi and auto-increment ID") {
    val insertCount = insertPois()
    val poi         = db.run(pois.create(Poi(0, "Padaria", 50, 50))).futureValue 
    val results     = db.run(pois.result).futureValue
    assert(results.size == 8)
    assert(poi.id == 8)
    assert(poi.name == "Padaria")
  }

  test("Delete a Poi by ID") {
    val insertCount = insertPois()
    var status      = db.run(pois.deleteById(3)).futureValue 
    var results     = db.run(pois.result).futureValue
    assert(results.size == 6)
    assert(status == 1) //found

    status          = db.run(pois.deleteById(3)).futureValue
    results         = db.run(pois.result).futureValue
    assert(results.size == 6)
    assert(status == 0) //not found
  }

  test("Get a Poi by ID") {
    val insertCount = insertPois()
    var poi: Option[Poi]     = db.run(pois.findById(3)).map(_.headOption).futureValue
    assert(poi.get.name == "Joalheria")

    poi = db.run(pois.findById(10)).map(_.headOption).futureValue
    assert(poi == None)
  }

  test("Find nearest Pois") {
    val insertCount = insertPois()
    // x = 20, y = 10, dmax = 10
    var nearPois: List[Poi] = db.run(pois.nearestNeighborSearch(20, 10, 10)).map(_.to[List]).futureValue
    assert(nearPois.map(_.name) == List("Lanchonete", "Joalheria", "Pub", "Supermercado"))

    // x = 1, y = 1, dmax = 3
    nearPois = db.run(pois.nearestNeighborSearch(1, 1, 3)).map(_.to[List]).futureValue
    assert(nearPois.map(_.name) == List())
  }
}
