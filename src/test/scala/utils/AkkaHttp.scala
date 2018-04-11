package utils

import org.scalatest.FunSuite
import webservice.AkkaClientUtils

class AkkaHttp extends FunSuite {

  test("create get url") {
    val url = "https://api.jcdecaux.com/vls/v1/stations"
    val arguments = Map(
      "apiKey" -> "1234567",
      "contract" -> "7654321"
    )

    assert(AkkaClientUtils.buildGetUrl(url, arguments) == "https://api.jcdecaux.com/vls/v1/stations?apiKey=1234567&contract=7654321")
  }

}
