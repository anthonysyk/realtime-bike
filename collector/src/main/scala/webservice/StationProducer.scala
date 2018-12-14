package webservice

import com.sksamuel.avro4s.RecordFormat
import config.AppConfig
import models.Station
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.ProducerRecord
import versatile.kafka.producer.KafkaProducerHelper

sealed trait StationProducerTrait {
  def sendStationsStatus(stations: Seq[Station]): Unit
}

class StationProducer(config: AppConfig) extends KafkaProducerHelper with StationProducerTrait {
  final val className = this.getClass.getName

  override val topic: String = config.kafka.station_topic
  override val sender: String = className
  override lazy val logsTopic: String = config.kafka.station_logs_topic
  override lazy val bootstrapServer: String = config.kafka.bootstrap_server

  val format: RecordFormat[Station] = RecordFormat[Station]

  private def createStationRecord(station: Station) = new ProducerRecord[String, GenericRecord](topic, station.externalId, format.to(station))

  override def sendStationsStatus(stations: Seq[Station]): Unit = {
    val records: Seq[ProducerRecord[String, GenericRecord]] = stations.map(createStationRecord)
    records.foreach(record => sendEventWithLogs(record))
  }

}


