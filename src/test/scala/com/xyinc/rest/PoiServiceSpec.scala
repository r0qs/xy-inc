package com.xyinc.rest

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.concurrent.Future

import org.scalatest.concurrent.ScalaFutures
import org.scalatest.BeforeAndAfter
import org.scalatest.FunSpec
import org.scalatest.Matchers
import spray.json._
import spray.http._
import StatusCodes._
import spray.httpx.SprayJsonSupport.sprayJsonMarshaller
import spray.httpx.SprayJsonSupport.sprayJsonUnmarshaller
import spray.testkit.ScalatestRouteTest

import com.xyinc.dto.Poi
import com.xyinc.dto.PoiJsonProtocol._
import com.xyinc.http.routes._

class PoiServiceSpec 
    extends FunSpec
    with Matchers
    with BeforeAndAfter
    with ScalatestRouteTest
    with PoiServiceRoute with ScalaFutures {

  implicit def actorSystem = system

  def actorRefFactory = system
  
  private implicit def ec = actorRefFactory.dispatcher

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

  describe("PoiFinder service") {

    it("the root path") {
      Get() ~> poisRoute ~> check {
        responseAs[String] should be("TODO: put routes preview here")
      }
    }

    it("return a MethodNotAllowed error for PUT requests to the root path") {
      Put() ~> sealRoute(poisRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET, POST, DELETE"
      }
    }

    it("return a Poi when insert it in database") {
      val poi = Poi(0, "Lanchonete", 23, 12)
      Post("/pois", poi) ~> poisRoute ~> check {
        val p = responseAs[Poi]

        status should be(Created)
        p.name should be(poi.name)
        p.id should be(1)
      }
    }

    it("return a Poi for GET requests to the /pois/id") {
      Get("/pois/1") ~> poisRoute ~> check {
        val p = responseAs[Poi]

        status should be(OK)
        p.name should be("Lanchonete")
        p.id should be(1)
      }
    }

    it("delete a Poi") {
      Delete("/pois/1") ~> poisRoute ~> check {
        val retCode = responseAs[String]
        
        status should be(OK)
        retCode should be("1")
      }
    } 

    it("Search nearest (dmax = 10) neighbors from: (x = 20, y = 10)") {
        val prePopulate = Await.result(populate, 5 seconds)
        prePopulate should be(true)
        val correctAnswer = List("Lanchonete", "Joalheria", "Pub", "Supermercado")
      Get("/pois/nearest?x=20&y=10&dmax=10") ~> poisRoute ~> check {
        val poisFound = responseAs[List[Poi]].map(_.name)
        status should be(OK)
        poisFound should be(correctAnswer)
      }
    } 
  }
}
