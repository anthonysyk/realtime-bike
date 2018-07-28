package webservice

import config.AppConfig
import io.circe.syntax._
import models.Station
import org.apache.kafka.clients.producer.ProducerRecord
import versatile.kafka.{EmbeddedKafkaProducerHelper, KafkaProducerHelper}

sealed trait StationProducerTrait {
  def sendStationsStatus(stations: Seq[Station]): Unit
}

class StationProducer(config: AppConfig) extends KafkaProducerHelper[String, String] with StationProducerTrait {
  override val topic: String = config.station_topic
  override val logsTopic: String = config.station_logs_topic

  private def createStationRecord(station: Station) = new ProducerRecord[String, String](topic, station.externalId, station.asJson.noSpaces)

  override def sendStationsStatus(stations: Seq[Station]): Unit = {
    val records: Seq[ProducerRecord[String, String]] = stations.map(createStationRecord)
    records.foreach(record => sendEventWithLogs(record))
  }

}

class StationProducerEmbedded(config: AppConfig) extends EmbeddedKafkaProducerHelper[String] with StationProducerTrait{
  override val topic: String = config.station_topic
  override val logsTopic: String = config.station_logs_topic


  private def createStationRecord(station: Station) = new ProducerRecord[String, String](topic, station.externalId, station.asJson.noSpaces)

  override def sendStationsStatus(stations: Seq[Station]): Unit = {
    val records: Seq[ProducerRecord[String, String]] = stations.map(createStationRecord)
    records.foreach(record => sendEventWithLogs(record))
  }
}


