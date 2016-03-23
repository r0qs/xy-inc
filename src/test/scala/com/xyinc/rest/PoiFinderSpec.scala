package com.xyinc.rest

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.concurrent.Future

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSpec
import org.scalatest.Matchers
import spray.json._
import spray.http._
import StatusCodes._
import spray.httpx.SprayJsonSupport
import spray.testkit.ScalatestRouteTest
import spray.routing.ValidationRejection
import spray.routing.HttpService

import com.xyinc.dto.Poi
import com.xyinc.dto.PoiJsonProtocol._
import com.xyinc.http.routes.PoiServiceRoute
import com.xyinc.dal.DAL
import com.xyinc.rest.Api
import com.xyinc.config.TestDatabaseSupport

class PoiFinderSpec 
    extends FunSpec
    with Matchers
    with BeforeAndAfter
    with ScalatestRouteTest
    with TestDatabaseSupport
    with DAL 
    with Api
    with HttpService
    with ScalaFutures {

  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  def actorRefFactory = system

  import com.xyinc.dto.PoiJsonProtocol._
  import SprayJsonSupport._

  def populate: Future[Boolean] = Future {                           
    val insertions: Seq[Future[Poi]] = Seq(     //IDS
      insertPoi(Poi(0, "Lanchonete", 27, 12)),  // 1
      insertPoi(Poi(0, "Posto", 31, 18)),       // 2
      insertPoi(Poi(0, "Joalheria", 15, 12)),   // 3
      insertPoi(Poi(0, "Floricultura", 19, 21)),// 4
      insertPoi(Poi(0, "Pub", 12, 8)),          // 5
      insertPoi(Poi(0, "Supermercado", 23, 6)), // 6
      insertPoi(Poi(0, "Churrascaria", 28, 2))  // 7
    )

    val aggregated: Future[Seq[Poi]] = Future.sequence(insertions)

    val pois: Seq[Poi] = Await.result(aggregated, 15 seconds)
    pois.size == 7
  }

  before {
    import driver.api._
    
    createDBSchema().futureValue
    val prePopulate = Await.result(populate, 5 seconds)
    prePopulate should be(true)
  }

  after {
    dropDBSchema().futureValue
  }

  describe("PoiFinder service operations:") {

    describe("PUT") {
      it("should return a MethodNotAllowed error for PUT requests to the root path") {
        Put() ~> sealRoute(routes) ~> check {
          status should be(MethodNotAllowed)
          responseAs[String] should be("HTTP method not allowed, supported methods: GET, POST, DELETE")
        }
      }
    }

    describe("GET") {
      it("should have nothing warning in the root path") {
        Get() ~> routes ~> check {
          responseAs[String] should be("")
        }
      }

      it("should be able to get all Pois") {
        Get("/pois/all") ~> routes ~> check {
          val poisFound = responseAs[List[Poi]]
          status should be(OK)
          poisFound.size should be(7)
        }
      }

      it("should be able to find a Poi by ID") {
        Get("/pois/1") ~> routes ~> check {
          val p = responseAs[Poi]

          status should be(OK)
          p.name should be("Lanchonete")
          p.id should be(1)
        }
      }

      describe("when search nearest neighbors") {
        val correctAnswer = List("Lanchonete", "Joalheria", "Pub", "Supermercado")

        it("should have only positive parameters") {
          Get("/pois/nearest?x=-20&y=10&dmax=-10") ~> sealRoute(routes) ~> check {
            responseAs[String] should be("ERROR: parameters must be a positive number!")
            status should be(BadRequest)
          }
        }

        it("should return a list of neighbors from a given coordinate: (x = 20, y = 10) with dmax = 10") {
          Get("/pois/nearest?x=20&y=10&dmax=10") ~> routes ~> check {
            val poisFound = responseAs[List[Poi]].map(_.name)
            status should be(OK)
            poisFound should be(correctAnswer)
          }
        }
      }
    }
    
    describe("POST") {
      it("should return a Poi when insert it in database") {
        val poi = Poi(0, "Pastelaria", 20, 12)
        Post("/pois", poi) ~> routes ~> check {
          val p = responseAs[Poi]

          status should be(Created)
          p.name should be(poi.name)
          p.id should be(8)
        }
      }

      it("should have only positive coordinates") {
        val poi = Poi(0, "Lanchonete", -1, 12)
        Post("/pois", poi) ~> sealRoute(routes) ~> check {
          responseAs[String] should be("ERROR: parameters must be a positive number!")
          status should be(BadRequest)
        }
      }
    }

    describe("DELETE") {
      it("should allow to delete a Poi") {
        Delete("/pois/1") ~> routes ~> check {
          val retCode = responseAs[String]
        
          status should be(OK)
          retCode should be("1")
        }
      } 
    }
  }
}
