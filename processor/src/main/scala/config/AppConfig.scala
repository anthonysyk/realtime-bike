package config

import com.typesafe.config.{Config, ConfigFactory}

case class AppConfig(
                      kafka: KafkaConf,
                      webservice: WebserviceConf
                    )

case class KafkaConf(
                      station_topic: String,
                      station_logs_topic: String,
                      bootstrap_server: String
                    )

case class WebserviceConf(
                         port: Int
                         )

object AppConfig {

  val appConfigFile: Config = ConfigFactory.load("application")

  val webserviceConf: Config = appConfigFile.getConfig("webservice")
  val kafkaConf: Config = appConfigFile.getConfig("kafka")

  val conf: AppConfig = AppConfig(
    KafkaConf(
      kafkaConf.getString("station_topic"),
      kafkaConf.getString("station_logs_topic"),
      kafkaConf.getString("bootstrap_server")
    ),
    WebserviceConf(
      webserviceConf.getInt("port")
    )
  )


}