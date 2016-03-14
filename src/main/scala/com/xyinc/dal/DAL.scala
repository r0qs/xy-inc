package com.xyinc.dal

import slick.jdbc.meta.MTable
import scala.concurrent.ExecutionContext.Implicits.global

import com.xyinc.config.Driver
import com.xyinc.dao.PoiDAO

trait DAL extends PoiDAO {
  this: Driver =>
  import driver.api._

  val schema = pois.schema

  // Create tables if not exists
  def createDBSchemaIfNotExists() = db.run(DBIO.seq(
    MTable.getTables map (tables => {
      if (!tables.exists(_.name.name == pois.baseTableRow.tableName))
        db.run(schema.create)
    })
  ))

  def createDBSchema() = db.run(schema.create)

  def dropDBSchema() = db.run(schema.drop)
}
