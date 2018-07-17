package config

class AppConfig {

  final val api_key = "0d23298797f5e87734c13efe39fcc4013dba6cf7"
  final val api_url = s"https://api.jcdecaux.com/vls/v1"
  final val brokers = "localhost:9092"
  final val station_topic = "Station"
  final val station_logs_topic = "StationLogs"
  final val environment = "local"

}
