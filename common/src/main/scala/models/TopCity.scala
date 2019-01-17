package models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class TopCity(
                    name: String,
                    bikes_taken: Int,
                    bikes_droped: Int
                  )

object TopCity {
  implicit val decoderStation: Decoder[TopCity] = deriveDecoder[TopCity]
  implicit val encoderStation: Encoder[TopCity] = deriveEncoder[TopCity]
}
