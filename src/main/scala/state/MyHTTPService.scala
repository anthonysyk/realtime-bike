package state

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import com.lightbend.kafka.scala.iq.http.InteractiveQueryHttpService
import org.apache.kafka.streams.state.HostInfo
import io.circe.parser._
import io.circe.syntax._

import scala.concurrent.ExecutionContext

class MyHTTPService(
                     hostInfo: HostInfo,
                     fetcher: StationStateFetcher,
                     actorSystem: ActorSystem,
                     actorMaterializer: ActorMaterializer,
                     ec: ExecutionContext
                   ) extends InteractiveQueryHttpService(hostInfo, actorSystem, actorMaterializer, ec) {

  // define the routes
  val routes: Flow[HttpRequest, HttpResponse, Any] = {
    pathPrefix("station") {
      (get & pathPrefix("access") & path(Segment)) { hostKey =>
        complete {
          fetcher.fetchWindowStationsStateByKey(hostKey).map(_.asJson)
        }
      }
    }
  }

}