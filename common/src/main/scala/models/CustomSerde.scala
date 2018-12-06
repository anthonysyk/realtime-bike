package models

import org.apache.kafka.common.serialization.Serde
import versatile.kafka.serde.SerdeHelper

object CustomSerde {

  val STATION_SERDE: Serde[Station] = SerdeHelper.createSerde[Station]

}
