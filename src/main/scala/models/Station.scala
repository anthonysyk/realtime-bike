package models

case class Station(
                    number: Int,
                    name: String,
                    address: String,
                    position: Position,
                    banking: Boolean,
                    bonus: Boolean,
                    status: String,
                    contract_name: String,
                    bike_stands: Int,
                    available_bike_stands: Int,
                    available_bikes: Int,
                    last_update: Long
                  )

object Station {
  import io.circe._
  import io.circe.generic.semiauto._
  import io.circe.parser._
  import utils.circe.CirceHelper._

  implicit val decoderStation: Decoder[Station] = deriveDecoder[Station]
  implicit val encoderStation: Encoder[Station] = deriveEncoder[Station]
  implicit val decoderPosition: Decoder[Position] = deriveDecoder[Position]
  implicit val encoderPosition: Encoder[Position] = deriveEncoder[Position]

  def fromStationListJson(string: String): Seq[Either[Error, Station]] = {
    parse(string).getRight.asArray.getOrElse(Nil).map(_.as[Station])
  }
}

case class Position(
                     lat: Double,
                     lng: Double
                   )