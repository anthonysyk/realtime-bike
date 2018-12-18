package state

import io.circe.parser._
import io.circe.syntax._
import models.{Station, StationState}
import utils.date.DateHelper
import versatile.json.CirceHelper._

object StateAggregators {

  def foldStationState: (String, Station, String) => String = { (aggKey, current, acc) =>
    val accStation = parse(acc).getRight.as[Station].right.toOption.get
    val isStateDefined = accStation.state.map(_.counter).isDefined

    val deltaBikes = current.available_bikes - accStation.available_bikes
    val bikesDropped = if(isStateDefined) {
      if (deltaBikes > 0) deltaBikes else 0 + accStation.state.map(_.bikes_droped).get
    } else 0
    val bikesTaken = if(isStateDefined) {
      if (deltaBikes < 0) deltaBikes else 0 + accStation.state.map(_.bikes_taken).get
    } else 0
    val availability = StationState.getAvailability(current.available_bikes, current.bike_stands)
    val start_date = accStation.state.map(_.start_date).getOrElse(DateHelper.convertToReadable(current.last_update))
    val counter = accStation.state.map(_.counter).getOrElse(0) + 1
    val counterStateChanged = if(accStation.copy(state = None).equals(current.copy(state = None))) {
      accStation.state.map(_.counterStateChanged).getOrElse(0)
    } else {
      accStation.state.map(_.counterStateChanged).getOrElse(0) + 1
    }

    current.copy(
      state = Some(StationState(
        start_date = start_date,
        last_update = DateHelper.convertToReadable(current.last_update),
        bikes_taken = bikesTaken,
        bikes_droped = bikesDropped,
        availability = availability,
        counter = counter,
        counterStateChanged = counterStateChanged
      ))
    ).asJson.noSpaces
  }

}
