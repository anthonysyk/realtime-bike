package models

import com.sksamuel.avro4s.RecordFormat
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import utils.{PathHelper, Writer}

case class StationKey(
                       contract_name: String,
                       number: Int,
                       last_update: Long
                     ) {
  lazy val externalId = s"${number}_$contract_name"
}

object StationKey {
  implicit val decoderStationKey: Decoder[StationKey] = deriveDecoder[StationKey]
  implicit val encoderStationKey: Encoder[StationKey] = deriveEncoder[StationKey]

  val avroFormat: RecordFormat[StationKey] = RecordFormat[StationKey]
}

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
                  ) {
  lazy val externalId = s"${number}_$contract_name"
  lazy val key = StationKey(contract_name, number, last_update)
}

object Station {

  import io.circe._
  import io.circe.generic.semiauto._
  import io.circe.parser._
  import versatile.json.CirceHelper._

  implicit val decoderStation: Decoder[Station] = deriveDecoder[Station]
  implicit val encoderStation: Encoder[Station] = deriveEncoder[Station]
  implicit val decoderPosition: Decoder[Position] = deriveDecoder[Position]
  implicit val encoderPosition: Encoder[Position] = deriveEncoder[Position]
  implicit val decoderStationState: Decoder[StationState] = deriveDecoder[StationState]
  implicit val encoderStationState: Encoder[StationState] = deriveEncoder[StationState]

  val avroFormat: RecordFormat[Station] = RecordFormat[Station]

  def fromStationListJson(string: String): Seq[Either[Error, Station]] = {
    parse(string).getRight.asArray.getOrElse(Nil).map(_.as[Station])
  }

  val empty = Station(
    number = 0,
    name = "no name",
    address = "no address",
    position = Position(0.0, 0.0),
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
                         start_date: String,
                         last_update: String,
                         bikes_taken: Int,
                         bikes_droped: Int,
                         availability: Double,
                         counter: Int,
                         counterStateChanged: Int
                       )

object StationState {

  implicit val format: RecordFormat[StationState] = RecordFormat[StationState]

  def getAvailability(used: Int, total: Int): Double = if (used != 0 && (total % used) != 0) {
    100 - (100 / (total.toDouble / used.toDouble))
  } else 100

}


case class StationReferential(id: String, number: Int, contract_name: String, address: String)

object StationReferential {

  import io.circe.generic.semiauto._

  implicit val encoder = deriveEncoder[StationReferential]
  implicit val decoder = deriveDecoder[StationReferential]

  def updateReferentials(stations: Seq[Station]) = {

    val ids = stations.map { s =>
      import s._
      StationReferential(
        externalId,
        number,
        contract_name,
        address
      )
    }.asJson.noSpaces

    Writer.write(s"${PathHelper.CollectorReference}/stations-ids.json", ids)
    println("Référentiel mis à jour")

  }
}
