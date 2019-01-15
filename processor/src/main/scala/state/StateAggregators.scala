package state

import io.circe.parser._
import io.circe.syntax._
import models.{Station, StationState}
import utils.date.DateHelper
import versatile.json.CirceHelper._

object StateAggregators {

  def foldStationState: (String, Station, String) => String = { (aggKey, currStation, acc) =>
    val accStation = parse(acc).getRight.as[Station].right.toOption.get

    val delta = accStation.available_bikes - currStation.available_bikes
    val bikesDroped = if (delta < 0) accStation.state.map(_.bikes_droped).getOrElse(0) + Math.abs(delta) else accStation.state.map(_.bikes_droped).getOrElse(0)
    val bikesTaken = if (delta > 0) accStation.state.map(_.bikes_taken).getOrElse(0) + Math.abs(delta) else accStation.state.map(_.bikes_taken).getOrElse(0)

    val availability = StationState.getAvailability(currStation.available_bikes, currStation.bike_stands)

    val start_date = accStation.state.map(_.start_date).getOrElse(DateHelper.convertToReadable(currStation.last_update))

    val counter = accStation.state.map(_.counter).getOrElse(0) + 1
    val counterStateChanged = if (accStation.copy(state = None).equals(currStation.copy(state = None))) {
      accStation.state.map(_.counterStateChanged).getOrElse(0)
    } else {
      accStation.state.map(_.counterStateChanged).getOrElse(0) + 1
    }

    currStation.copy(
      state = Some(StationState(
        start_date = start_date,
        last_update = DateHelper.convertToReadable(currStation.last_update),
        bikes_taken = if (accStation.state.exists(_.counter > 0)) bikesTaken else 0,
        bikes_droped = if (accStation.state.exists(_.counter > 0)) bikesDroped else 0,
        availability = availability,
        counter = counter,
        counterStateChanged = counterStateChanged
      ))
    ).asJson.noSpaces
  }

}
