package config

import com.typesafe.config.{Config, ConfigFactory}

case class AppConfig(
                      kafka: KafkaConf,
                      webservice: WebserviceConf
                    )

case class KafkaConf(
                      station_topic: String,
                      station_raw_topic: String,
                      station_state_topic: String,
                      bootstrap_server: String
                    )

case class WebserviceConf(
                           host: String,
                           port: Int
                         ) {
  val hostport = s"$host:$port"
}

object AppConfig {

  val appConfigFile: Config = ConfigFactory.load("application")

  val webserviceConf: Config = appConfigFile.getConfig("webservice")
  val kafkaConf: Config = appConfigFile.getConfig("kafka")

  val conf: AppConfig = AppConfig(
    KafkaConf(
      kafkaConf.getString("station_topic"),
      kafkaConf.getString("station_raw_topic"),
      kafkaConf.getString("station_state_topic"),
      kafkaConf.getString("bootstrap_server")
    ),
    WebserviceConf(
      webserviceConf.getString("host"),
      webserviceConf.getInt("port")
    )
  )


}
