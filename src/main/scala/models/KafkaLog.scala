package models

import org.apache.kafka.clients.producer.{ProducerRecord, RecordMetadata}

case class KafkaLog(
                     fid: Int,
                     offset: Long,
                     partition: Int,
                     topic: String,
                     timestamp: Long,
                     value: String
                   )

object KafkaLog {
  import io.circe._
  import io.circe.generic.semiauto._
  import io.circe.parser._
  import utils.circe.CirceHelper._

  implicit val encoder: Encoder[KafkaLog] = deriveEncoder[KafkaLog]
  implicit val decoder: Decoder[KafkaLog] = deriveDecoder[KafkaLog]


  def fromRecordMetadata(meta: RecordMetadata, fid: Int, value: String) = {
    KafkaLog(
      fid,
      meta.offset(),
      meta.partition(),
      meta.topic(),
      meta.timestamp(),
      value
    )
  }

}
