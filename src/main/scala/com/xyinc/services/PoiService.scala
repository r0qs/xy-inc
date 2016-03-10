package com.xyinc.services

import com.xyinc.dto.Poi
import com.xyinc.dao.PoiDAO

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait PoiService {
  
  private val poiDAO = new PoiDAO 

  def getAllPois(): Future[List[Poi]] = poiDAO.getAll.map(_.toList)
  
  def insertPoi(poi: Poi): Future[Poi] = poiDAO.insert(poi)
  
  def getPoi(id: Int): Future[Option[Poi]] = poiDAO.get(id)
  
  def searchNearestPois(x: Int, y: Int, dmax: Int) : Future[List[Poi]] = poiDAO.searchNearest(x, y, dmax).map(_.to[List])
  
  def deletePoi(id: Int): Future[String] = poiDAO.delete(id).map(_.toString)

  def clearDB(): Future[Unit] = poiDAO.clear
}

