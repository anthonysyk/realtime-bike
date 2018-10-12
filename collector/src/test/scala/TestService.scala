import config.AppConfig
import webservice.BikeApiService

class TestService {

  import concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]): Unit = {
    val service = new BikeApiService(AppConfig.conf)

    service.getStationInformation(9087, "Marseille").andThen { case response =>
      println(response)
      service.http.shutdownAllConnectionPools()
      service.system.terminate()
    }
  }

}
