package com.xyinc.config

import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import com.xyinc.config.DatabaseSupport

trait TestDatabaseSupport extends DatabaseSupport {
  private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("database.test")
  val driver = dbConfig.driver
  val db = dbConfig.db
}
