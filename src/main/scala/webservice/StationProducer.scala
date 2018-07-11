package webservice

import config.AppConfig
import io.circe.syntax._
import kafka.KafkaProducerHelper
import models.Station
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer


class StationProducer extends KafkaProducerHelper[String, String] {
  override val topic: String = AppConfig.station_topic
  override val logsTopic: String = AppConfig.station_logs_topic
  override def keySerializer: String = classOf[StringSerializer].getName
  override def valueSerializer: String = classOf[StringSerializer].getName

  def createStationRecord(station: Station) = new ProducerRecord[String, String](topic, station.externalId, station.asJson.noSpaces)

  def sendStationsStatus(stations: Seq[Station]): Unit = {
    val records: Seq[ProducerRecord[String, String]] = stations.map(createStationRecord)
    records.foreach(record => sendEventWithLogs(record))
  }

}
