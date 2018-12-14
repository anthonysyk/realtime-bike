package models

case class Coordinates(topLeft: Double,
                       bottomRight: Double,
                       bottomLeft: Double,
                       topRight: Double
                      )

object Coordinates {
  import io.circe._
  import io.circe.generic.semiauto._

  implicit val decoderCoordinates: Decoder[Coordinates] = deriveDecoder[Coordinates]
  implicit val encoderCoordinates: Encoder[Coordinates] = deriveEncoder[Coordinates]}
