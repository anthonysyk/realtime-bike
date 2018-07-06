package webservice

import config.AppConfig
import io.circe.syntax._
import kafka.KafkaProducerHelper
import models.Station
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}


class StationProducer extends KafkaProducerHelper[Int, String] {
  override val topic: String = AppConfig.station_topic
  override val logsTopic: String = AppConfig.station_logs_topic
  override def keySerializer: String = classOf[IntegerSerializer].getName
  override def valueSerializer: String = classOf[StringSerializer].getName

  def createStationRecord(station: Station) = new ProducerRecord[Int, String](topic, station.number, station.asJson.noSpaces)

  def sendStationsStatus(stations: Seq[Station]): Unit = {
    val records: Seq[ProducerRecord[Int, String]] = stations.map(createStationRecord)
    records.foreach(record => sendEventWithLogs(record))
  }

}
