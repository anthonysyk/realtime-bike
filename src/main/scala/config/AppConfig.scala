package config

import config.AppConfig.Environnement

class AppConfig {

  final val api_key = "0d23298797f5e87734c13efe39fcc4013dba6cf7"
  final val api_url = s"https://api.jcdecaux.com/vls/v1"
  final val brokers = "localhost:9092"
  final val station_topic = "Station"
  final val station_logs_topic = "StationLogs"
  final val environment = Environnement.PRODUCTION

  // STIF

  final val stif_api_key = "4fd1fe1a1781fce636e5e8d7a50b3e4bf1358ef3ede4bcf886709d12"

}


object AppConfig {

  object Environnement extends Enumeration {
    val LOCAL = Value("local")
    val PRODUCTION = Value("production")
  }

}