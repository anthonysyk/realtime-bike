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
                    last_update: Long,
                    state: Option[StationState]
                  )

object Station {
  import io.circe._
  import io.circe.generic.semiauto._
  import io.circe.parser._
  import utils.CirceHelper._

  implicit val decoderStation: Decoder[Station] = deriveDecoder[Station]
  implicit val encoderStation: Encoder[Station] = deriveEncoder[Station]
  implicit val decoderPosition: Decoder[Position] = deriveDecoder[Position]
  implicit val encoderPosition: Encoder[Position] = deriveEncoder[Position]
  implicit val decoderStationState: Decoder[StationState] = deriveDecoder[StationState]
  implicit val encoderStationState: Encoder[StationState] = deriveEncoder[StationState]

  def fromStationListJson(string: String): Seq[Either[Error, Station]] = {
    parse(string).getRight.asArray.getOrElse(Nil).map(_.as[Station])
  }

  val empty = Station(
    number = 0,
    name = "no name",
    address = "no address",
    position = Position(0.0,0.0),
    banking = false,
    bonus = false,
    status = "no status",
    contract_name = "no contract name",
    bike_stands = 0,
    available_bike_stands = 0,
    available_bikes = 0,
    last_update = 0,
    state = None
  )
}

case class Position(lat: Double, lng: Double)

case class StationState(
                         bikes_taken: Int,
                         bikes_droped: Int,
                         availability: Int
                       )