package models

import java.util

import io.confluent.kafka.streams.serdes.avro.{GenericAvroDeserializer, GenericAvroSerializer}
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.{Serde, Serdes}
import versatile.kafka.serde.SerdeHelper

object CustomSerde {

  val STATION_SERDE: Serde[Station] = SerdeHelper.createSerde[Station]

  val TOP_STATION_SERDE: Serde[TopStation] = SerdeHelper.createSerde[TopStation]

  val WINDOW_STATION_SERDE: Serde[WindowStation] = SerdeHelper.createSerde[WindowStation]

  def genericAvroSerde: Serde[GenericRecord] = {
    import scala.collection.JavaConverters._
    val schemaRegistry: util.Map[String, String] = Map("schema.registry.url" -> "http://localhost:8081").asJava

    Serdes.serdeFrom({
      val ser = new GenericAvroSerializer()
      ser.configure(schemaRegistry, false)
      ser
    }, {
      val de = new GenericAvroDeserializer()
      de.configure(schemaRegistry, false)
      de
    })
  }

}
