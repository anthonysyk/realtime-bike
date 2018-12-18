package config

import com.typesafe.config.{Config, ConfigFactory}

case class AppConfig(
                      api: ApiConf,
                      kafka: KafkaConf
                    )

case class ApiConf(
                    api_url: String,
                    api_key: String
                  )

case class KafkaConf(
                      station_topic: String,
                      station_raw_topic: String,
                      station_structured_topic: String,
                      bootstrap_server: String
                    )

object AppConfig {

  val appConfigFile: Config = ConfigFactory.load("application")

  val apiConf: Config = appConfigFile.getConfig("api")
  val kafkaConf: Config = appConfigFile.getConfig("kafka")

  val conf: AppConfig = AppConfig(
    ApiConf(
      apiConf.getString("api_url"),
      apiConf.getString("api_key")
    ),
    KafkaConf(
      kafkaConf.getString("station_topic"),
      kafkaConf.getString("station_raw_topic"),
      kafkaConf.getString("station_structured_topic"),
      kafkaConf.getString("bootstrap_server")
    )
  )


}
