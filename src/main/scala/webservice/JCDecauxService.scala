package webservice

import config.AppConfig

import scala.concurrent.Future

class JCDecauxService() extends ServiceHelper {

  val stationsUrl = s"${AppConfig.api_url}/stations"

  val contractsUrl = s"${AppConfig.api_url}/contracts"

  val apiKeyMap = Map(
    "apiKey" -> AppConfig.api_key
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

object TestService {

  import concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]): Unit = {
    val service = new JCDecauxService()

    service.getStationInformation(9087, "Marseille").andThen { case response =>
      println(response)
      service.http.shutdownAllConnectionPools()
      service.system.terminate()
    }
  }

}
