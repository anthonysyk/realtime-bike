package state

import java.util.concurrent.Executors

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.lightbend.kafka.scala.iq.http.{HttpRequester, KeyValueFetcher}
import com.lightbend.kafka.scala.iq.services.{LocalStateStoreQuery, MetadataService}
import state.StationStateProcessor.{HOST_INFO, streams}

import scala.concurrent.ExecutionContext

object MainStateApi {

  def main(args: Array[String]): Unit = {

    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    lazy val defaultParallelism: Int = {
      val rt = Runtime.getRuntime
      rt.availableProcessors() * 4
    }

    def defaultExecutionContext(parallelism: Int = defaultParallelism): ExecutionContext =
      ExecutionContext.fromExecutor(Executors.newFixedThreadPool(parallelism))

    val executionContext = defaultExecutionContext()

    streams.start()

    // Step 1: service for fetching metadata information
    val metadataService = new MetadataService(streams)

    // Step 2: service for fetching from local state store
    val localStateStoreQuery = new LocalStateStoreQuery[String, Long]

    // Step 3: http service for request handling
    val httpRequester = new HttpRequester(system, materializer, executionContext)

    val fetcher = new StationStateFetcher(
      new KeyValueFetcher(metadataService, localStateStoreQuery, httpRequester, streams, executionContext, HOST_INFO)
    )

    // Step 4: create custom http application service
    val restService = new MyHTTPService(
      HOST_INFO, fetcher, system, materializer, executionContext
    )

    // enjoy!
    restService.start()

  }

}
