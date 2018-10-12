package webservice

import config.AppConfig
import versatile.web.WebClient

import scala.concurrent.Future

class BikeApiService(config: AppConfig) extends WebClient {

  val stationsUrl = s"${config.api.api_url}/stations"

  val contractsUrl = s"${config.api.api_url}/contracts"

  val apiKeyMap = Map(
    "apiKey" -> config.api.api_key
  )

  def getContactsList: Future[Either[String, String]] = doGet(contractsUrl)


  def getStationInformation(stationNumber: Int, contractName: String): Future[Either[String, String]] = {
    val args = apiKeyMap + ("contract" -> contractName)

    doGet(s"$stationsUrl/$stationNumber", args)

  }

  def getStationsList: Future[Either[String, String]] = doGet(stationsUrl, apiKeyMap)

  def getStationsByContract(contractName: String): Future[Either[String, String]] = {
    val args = apiKeyMap + ("contract" -> contractName)

    doGet(stationsUrl, args)
  }

  val parisStations = "https://www.velib-metropole.fr/webapi/map/details?gpsTopLatitude=50.92472720355735&gpsTopLongitude=2.526699071550297&gpsBotLatitude=48.80050394038224&gpsBotLongitude=2.1061286980111618&zoomLevel=14"

  def getParisStations = doGet(parisStations)

}