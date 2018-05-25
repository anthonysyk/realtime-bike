package models

import org.apache.kafka.clients.producer.RecordMetadata

case class KafkaLog(
                     fid: Int,
                     offset: Long,
                     partition: Int,
                     topic: String,
                     timestamp: Long,
                     value: String,
                     isSuccess: Boolean
                   )

object KafkaLog {

  import io.circe._
  import io.circe.generic.semiauto._

  implicit val encoder: Encoder[KafkaLog] = deriveEncoder[KafkaLog]
  implicit val decoder: Decoder[KafkaLog] = deriveDecoder[KafkaLog]


  def fromRecordMetadata(meta: RecordMetadata, fid: Int, value: String, isSuccess: Boolean) = {
    KafkaLog(
      fid,
      meta.offset(),
      meta.partition(),
      meta.topic(),
      meta.timestamp(),
      value,
      isSuccess
    )
  }

}
