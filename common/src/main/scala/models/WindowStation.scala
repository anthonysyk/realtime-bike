package models

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

case class WindowStation(
                          externalId: String,
                          name: String,
                          available_bike_stands: Int,
                          available_bikes: Int,
                          last_update_ts: Long,
                          last_update: String,
                          start_date: String,
                          bikes_taken: Int,
                          bikes_droped: Int,
                          counter: Int
                        )

object WindowStation {
  implicit val decoder: Decoder[WindowStation] = deriveDecoder[WindowStation]
  implicit val encoder: Encoder[WindowStation] = deriveEncoder[WindowStation]

  def empty = WindowStation(
    externalId = "",
    name = "",
    available_bike_stands = 0,
    available_bikes = 0,
    last_update_ts = 0L,
    last_update = "",
    start_date = "",
    bikes_taken = 0,
    bikes_droped = 0,
    counter = 0
  )
}
