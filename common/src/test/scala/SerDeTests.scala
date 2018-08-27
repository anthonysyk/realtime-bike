import io.circe.parser._
import io.circe.syntax._
import models.Station
import versatile.utils.CirceHelper._
import minitest._


object SerDeTests extends SimpleTestSuite {

  val stationJson = """[{"number":9087,"name":"9087-MAZARGUES","address":"MAZARGUES - ROND POINT DE MAZARGUES (OBELISQUE)","position":{"lat":43.250903869637334,"lng":5.403244616491982},"banking":true,"bonus":false,"status":"OPEN","contract_name":"Marseille","bike_stands":21,"available_bike_stands":4,"available_bikes":17,"last_update":1523552815000},{"number":280,"name":"280 - HEYSEL / HEISEL","address":"HEYSEL / HEISEL - AVENUE DE L'IMPERATRICE CHARLOTTE / KEIZERIN CHARLOTTELAAN","position":{"lat":50.897522,"lng":4.334831},"banking":true,"bonus":false,"status":"OPEN","contract_name":"Bruxelles-Capitale","bike_stands":25,"available_bike_stands":13,"available_bikes":12,"last_update":1523552535000}]"""

  test("serde station") {

    val result = Station.fromStationListJson(stationJson)

    result.foreach(println)

  }

  test("serde tuple") {

    val tuple = 1 -> 2

    val bytes = tuple.asJson.noSpaces.getBytes()

    println(parse(new String(bytes)).getRight.as[(Int, Int)].right.toOption)


  }



}
