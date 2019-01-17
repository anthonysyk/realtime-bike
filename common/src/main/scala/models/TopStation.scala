package models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class TopStation(
                       number: Int,
                       name: String,
                       address: String,
                       status: String,
                       contract_name: String,
                       available_bike_stands: Int,
                       available_bikes: Int,
                       bikes_droped: Int,
                       bikes_taken: Int,
                       totalBikes: Seq[Int],
                       delta: Seq[Int],
                       counter: Int,
                       start_date: String,
                       last_update: String,
                       start_timestamp: Long
                     ) {
}

object TopStation {

  implicit val decoderStation: Decoder[TopStation] = deriveDecoder[TopStation]
  implicit val encoderStation: Encoder[TopStation] = deriveEncoder[TopStation]

  def empty = {
    val station = Station.empty
    TopStation(
      number = station.number,
      name = station.name,
      address = station.address,
      status = station.status,
      contract_name = station.contract_name,
      available_bike_stands = station.available_bike_stands,
      available_bikes = station.available_bikes,
      bikes_droped = 0,
      bikes_taken = 0,
      totalBikes = Nil,
      delta = Nil,
      counter = 0,
      start_date = "",
      last_update = "",
      start_timestamp = 0L
    )
  }

  def cleanupName(city: String, name: String): String = {
    city match {
      case "Luxembourg" => name.drop(7)
      case "Nancy" => name.split(" - ", 2).lift(1).getOrElse(name)
      case "Paris" => name
      case "Toulouse" => name.split("- ", 2).lift(1).getOrElse(name)
      case "Nantes" => name.drop(7)
      case "Marseille" => name.split("-", 2).lift(1).getOrElse(name)
      case "Lyon" => name.split("-", 2).lift(1).getOrElse(name)
      case "Amiens" => name.split("- ", 2).lift(1).getOrElse(name)
      case "Creteil" => name.split("-", 2).lift(1).getOrElse(name)
      case "Rouen" => name.split("-", 2).lift(1).getOrElse(name)
    }
  }
}
