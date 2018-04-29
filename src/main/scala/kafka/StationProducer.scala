package kafka

import java.util.concurrent.Future

import io.circe.syntax._
import models.{KafkaLog, Station}
import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord, RecordMetadata}


class StationProducer {

  import org.apache.kafka.clients.producer.KafkaProducer

  val props = new java.util.Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.CLIENT_ID_CONFIG, "StationProducer")
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer")

  val stationProducer = new KafkaProducer[Integer, Array[Byte]](props)

  val logsProducer = new KafkaProducer[Integer, Array[Byte]](props)

  def createStationRecord(station: Station) = new ProducerRecord[Integer, Array[Byte]](Topics.STATION_TOPIC, station.number, station.asJson.noSpaces.getBytes)

  def sendStationsStatus(stations: Seq[Station]): Seq[Future[RecordMetadata]] = {

    val records = stations.map(createStationRecord)

    // TODO: make it generic PublishAndLog
    records.map { record =>
      val eventuallyMeta: Future[RecordMetadata] = stationProducer.send(record)

      val kafkaLog: KafkaLog = KafkaLog.fromRecordMetadata(eventuallyMeta.get(), record.key(), new String(record.value()))

      val logRecord = new ProducerRecord[Integer, Array[Byte]](Topics.STATION_LOGS_TOPIC, kafkaLog.fid, kafkaLog.asJson.noSpaces.getBytes)

      logsProducer.send(logRecord)
    }

  }


}
