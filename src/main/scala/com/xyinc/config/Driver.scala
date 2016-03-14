package com.xyinc.config

import slick.driver.JdbcProfile

// Database driver profile
trait Driver {
  val driver: JdbcProfile
}
