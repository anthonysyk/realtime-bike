package kafka

import io.circe.syntax._
import models.Station
import org.apache.kafka.clients.producer.ProducerRecord


class StationProducer extends KafkaProducerHelper[Int, Array[Byte]] {
  override val topic: String = Topics.STATION_TOPIC
  override val logsTopic: String = Topics.STATION_LOGS_TOPIC

  def createStationRecord(station: Station) = new ProducerRecord[Int, Array[Byte]](Topics.STATION_TOPIC, station.number, station.asJson.noSpaces.getBytes)

  def sendStationsStatus(stations: Seq[Station]): Unit = {

    val records: Seq[ProducerRecord[Int, Array[Byte]]] = stations.map(createStationRecord)

    records.foreach(record => sendEventWithLogs(record))

  }
}
