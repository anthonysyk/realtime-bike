package webservice

import config.AppConfig
import versatile.web.WebClient

import scala.concurrent.Future

class JCDecauxService(config: AppConfig) extends WebClient {

  val stationsUrl = s"${config.api_url}/stations"

  val contractsUrl = s"${config.api_url}/contracts"

  val apiKeyMap = Map(
    "apiKey" -> config.api_key
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


}