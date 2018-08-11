package models

import versatile.kafka.serde.SerdeHelper

object Serde {

  val STATION_SERDE = SerdeHelper.createSerde[Station]

}
