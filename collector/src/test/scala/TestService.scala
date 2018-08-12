import config.AppConfig
import webservice.JCDecauxService

class TestService {

  import concurrent.ExecutionContext.Implicits.global

  def main(args: Array[String]): Unit = {
    val service = new JCDecauxService(AppConfig.conf)

    service.getStationInformation(9087, "Marseille").andThen { case response =>
      println(response)
      service.http.shutdownAllConnectionPools()
      service.system.terminate()
    }
  }

}
