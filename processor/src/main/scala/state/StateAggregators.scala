package state

import io.circe.parser._
import io.circe.syntax._
import models.{Station, StationState, TopStation, WindowStation}
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
      name = TopStation.cleanupName(currStation.contract_name, currStation.name),
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

  type JsonString = String

  def foldWindowStation: (String, Station, JsonString) => JsonString = { (_, currStation, stringAcc) =>
    val windowStation = parse(stringAcc).getRight.as[WindowStation].right.toOption.get

    val delta = windowStation.available_bikes - currStation.available_bikes
    val bikesDroped = if (delta < 0) windowStation.bikes_droped + Math.abs(delta) else windowStation.bikes_droped
    val bikesTaken = if (delta > 0) windowStation.bikes_taken + Math.abs(delta) else windowStation.bikes_taken

    val start_date = if(windowStation.counter == 0) DateHelper.convertToReadable(currStation.last_update) else windowStation.start_date

    val counter = windowStation.counter + 1

    WindowStation(
      externalId = currStation.externalId,
      name = TopStation.cleanupName(currStation.contract_name, currStation.name),
      available_bike_stands = currStation.available_bike_stands,
      available_bikes = currStation.available_bikes,
      last_update_ts = currStation.last_update,
      last_update = DateHelper.convertToReadable(currStation.last_update),
      start_date = start_date,
      bikes_taken = if (windowStation.counter > 0) bikesTaken else 0,
      bikes_droped = if (windowStation.counter > 0) bikesDroped else 0,
      counter = counter
    ).asJson.noSpaces
  }

}
