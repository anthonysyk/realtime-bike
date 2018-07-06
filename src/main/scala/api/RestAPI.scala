package api

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import deprecated.StationStateVanilla
import io.circe.syntax._

import scala.concurrent.ExecutionContextExecutor


object RestAPI {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem("rest-api")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val route: Route =
      path("state" / IntNumber) { int =>
        get {
          complete(
            HttpEntity(ContentTypes.`text/html(UTF-8)`,
              //              stationState.view.get(int).get.asJson.noSpaces)
              ""
            )
          )
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

    println(s"Server online at http://localhost:8080/")
  }

}
