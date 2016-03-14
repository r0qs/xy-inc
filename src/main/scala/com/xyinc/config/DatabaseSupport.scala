package com.xyinc.config

import slick.backend.DatabaseConfig
import slick.driver._
import slick.jdbc.JdbcBackend._

trait DatabaseSupport extends Driver {
  val db: JdbcProfile#Backend#Database
}

trait ConfigDatabaseSupport extends DatabaseSupport {
  private val dbConfig: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig("database.default")
  val driver = dbConfig.driver
  val db = dbConfig.db
}
