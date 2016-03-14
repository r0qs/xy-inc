package com.xyinc.config

import com.typesafe.config.ConfigFactory

object AppConfig {

  private val config = ConfigFactory.load()

  /**
   * Configuration information of the service
   */
  object PoiService {
    private val serviceConfig = config.getConfig("app")
    lazy val hostname = serviceConfig.getString("interface")
    lazy val port = serviceConfig.getInt("port")
  }
}
