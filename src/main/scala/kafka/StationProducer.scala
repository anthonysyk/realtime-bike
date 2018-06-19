package kafka

import io.circe.syntax._
import models.Station
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{IntegerSerializer, StringSerializer}


class StationProducer extends KafkaProducerHelper[Int, String] {
  override val topic: String = Topics.STATION_TOPIC
  override val logsTopic: String = Topics.STATION_LOGS_TOPIC
  override def keySerializer: String = classOf[IntegerSerializer].getName
  override def valueSerializer: String = classOf[StringSerializer].getName

  def createStationRecord(station: Station) = new ProducerRecord[Int, String](topic, station.number, station.asJson.noSpaces)

  def sendStationsStatus(stations: Seq[Station]): Unit = {

    val records: Seq[ProducerRecord[Int, String]] = stations.map(createStationRecord)

    records.foreach(record => sendEventWithLogs(record))

  }

}
