package models

import utils.date.DateHelper

import scala.util.Try

case class SummaryParisStation(
                                stationInfo: StationInfo,
                                nbBike: Int,
                                nbEbike: Int,
                                nbFreeDock: Int,
                                nbFreeEDock: Int,
                                nbDock: Int,
                                nbEDock: Int
                              )

case class StationInfo(
                        gps: GPS,
                        state: String,
                        name: String,
                        code: String,
                        `type`: String,
                        dueDate: Long
                      )

case class GPS(
                latitude: Double,
                longitude: Double
              )

case class ParisStation(
                         station: StationInfo,
                         nbBike: Int,
                         nbEbike: Int,
                         nbFreeDock: Int,
                         nbFreeEDock: Int,
                         creditCard: String,
                         nbDock: Int,
                         nbEDock: Int,
                         nbBikeOverflow: Int,
                         nbEBikeOverflow: Int,
                         kioskState: String,
                         overflow: String,
                         overflowActivation: String,
                         maxBikeOverflow: Int,
                         densityLevel: Int
                       ) {

  val stationValue = Station(
    number = Try {
      station.code.toInt
    }.toOption.getOrElse(0),
    name = station.name,
    address = station.name,
    position = Position(
      lat = Try {
        station.gps.latitude.toDouble
      }.toOption.getOrElse(0),
      lng = Try {
        station.gps.longitude.toDouble
      }.toOption.getOrElse(0)),
    banking = creditCard == "yes",
    bonus = false,
    status = station.state match {
      case "Operative" => "OPEN"
      case _ => "CLOSED"
    },
    contract_name = "Paris",
    bike_stands = nbDock + nbEDock,
    available_bike_stands = nbFreeDock + nbFreeEDock,
    available_bikes = nbBike + nbEbike,
    last_update = DateHelper.nowTimestamp,
    state = None
  )

  lazy val summary = SummaryParisStation(
    station,
    nbBike = nbBike,
    nbEbike = nbEbike,
    nbFreeDock = nbFreeEDock,
    nbFreeEDock = nbFreeEDock,
    nbDock = nbDock,
    nbEDock = nbEDock
  )

  def toStation(previous: Option[SummaryParisStation]): Option[Station] = previous match {
    case Some(previousSummary) => if(previousSummary.equals(this.summary)) None else Option(this.stationValue)
    case None => Option(this.stationValue)
  }

}

object ParisStation {

  import io.circe._
  import io.circe.generic.semiauto._
  import io.circe.parser._
  import versatile.json.CirceHelper._

  implicit val decoderStationInfo: Decoder[StationInfo] = deriveDecoder[StationInfo]
  implicit val encoderStationInfo: Encoder[StationInfo] = deriveEncoder[StationInfo]
  implicit val decoderGPS: Decoder[GPS] = deriveDecoder[GPS]
  implicit val encoderGPS: Encoder[GPS] = deriveEncoder[GPS]
  implicit val decoderVelibStation: Decoder[ParisStation] = deriveDecoder[ParisStation]
  implicit val encoderVelibStation: Encoder[ParisStation] = deriveEncoder[ParisStation]

  def fromStationListJson(string: String, currentState: Map[String, SummaryParisStation]): Seq[Either[String, Option[(Station, SummaryParisStation)]]] = {
    parse(string).getRight.asArray.getOrElse(Nil).map(_.as[ParisStation] match {
      case Right(station) =>
        Right(station.toStation(currentState.get(station.station.name)).map(_ -> station.summary))
      case Left(decodingError) =>
        println(s"Erreur de sérialisation ${decodingError.message}")
        Left(decodingError.message)
    })
  }
}
