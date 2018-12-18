package webservice

import config.AppConfig
import io.circe.syntax._
import models.{Station, StationKey}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.ProducerRecord
import versatile.kafka.models.VFullRecord
import versatile.kafka.producer.KafkaProducerHelper

sealed trait StationProducerTrait {
  def sendStationEvents(stations: Seq[Station], eventType: String): Unit

  def sendRawResponseEvent(source: String, response: String, eventType: String): Unit
}

class StationProducer(config: AppConfig) extends KafkaProducerHelper with StationProducerTrait {
  final val className = this.getClass.getName

  override val topic: String = config.kafka.station_topic
  override val persistEventTopic: String = config.kafka.station_structured_topic
  override val source: String = className
  override lazy val bootstrapServer: String = config.kafka.bootstrap_server

  private def createStationRecord(station: Station, eventType: String) = VFullRecord(
    None,
    eventType,
    new ProducerRecord[GenericRecord, GenericRecord](topic, StationKey.avroFormat.to(station.key), Station.avroFormat.to(station)),
    station.asJson
  )

  override def sendStationEvents(stations: Seq[Station], eventType: String): Unit = {
    val records: Seq[VFullRecord] = stations.map(station => createStationRecord(station, eventType))
    records.foreach(record => sendEventWithRaw(record))
  }

  override def sendRawResponseEvent(rawSource: String, response: String, eventType: String): Unit = sendEventRaw(config.kafka.station_raw_topic, eventType, Some(rawSource), response)

}


