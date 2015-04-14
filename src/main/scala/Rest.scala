import akka.actor.{Props, ActorSystem}
import akka.io.IO
import akka.util.Timeout
import com.vixxx123.rest.ApiService
import com.vixxx123.rest.internal.logger.{ConsoleLogger, Logger}
import spray.can.Http
import scala.concurrent.duration._
import akka.pattern.ask

/**
 * Created by Wiktor Tychulski on 2014-11-16.
 */


object Rest extends App {
  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")

  // start up logger actor system and logger actor
  Logger.LoggingActorSystem.actorOf(Props(classOf[Logger], List(new ConsoleLogger)), Logger.LoggerActorName)

  // start up API service actor
  val service = system.actorOf(Props[ApiService], "example-rest-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}
