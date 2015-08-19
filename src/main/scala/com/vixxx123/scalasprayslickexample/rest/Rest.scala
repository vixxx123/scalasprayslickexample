/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.rest

import akka.actor.{ActorSystem, PoisonPill}
import akka.io.IO
import akka.util.Timeout
import com.vixxx123.scalasprayslickexample.logger.{Logger, LoggingService}
import com.vixxx123.scalasprayslickexample.rest.auth.{NoAuthorisation, Authorization}
import com.vixxx123.scalasprayslickexample.websocket.WebSocketServer
import spray.can.Http
import spray.can.server.UHttp

import scala.concurrent.duration._

/**
 * Rest service main class
 *
 * run start method on this obejct in order to start it up
 *
 * @param actorSystem - actor system in which rest service will be placed
 * @param listOfResourceApiBuilders - list of BaseResourceBuilder
 * @param loggers - list of loggers
 * @param authorization - authorisation method - default: NoAuthorisation
 */
class Rest(actorSystem: ActorSystem, listOfResourceApiBuilders: List[BaseResourceBuilder], loggers: List[Logger],
           authorization: Authorization = NoAuthorisation) {

  // we need an ActorSystem to host our application in
  implicit val system = actorSystem

  def start(): Unit = {

    println("Starting up...")
    // start up logger actor system and logger actor
    LoggingService.init(loggers)

    authorization.init()

    // start up API service actor
    val apis = authorization.getAuthApiBuilder match {
      case Some(authResourceBuilder) =>
        listOfResourceApiBuilders :+ authResourceBuilder
      case None => listOfResourceApiBuilders
    }


    val service = system.actorOf(ApiService.props(apis, authorization), ApiService.ActorName)
    val server = system.actorOf(WebSocketServer.props(authorization), WebSocketServer.Name)


    println("Websocket is starting...")
    implicit val timeout = Timeout(5.seconds)
    // start a new HTTP server on port 8080 with our service actor as the handler
    IO(UHttp) ! Http.Bind(server, "localhost", port = 8082)


    println("Rest is starting...")
    // SPRAY WORKAROUND: Must me killed before starting rest server because of actor naming collision
    system.actorSelection("/user/IO-HTTP") ! PoisonPill
    Thread.sleep(1000)
    IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)
  }

  def stop(): Unit = {
    println("Shouting down...")
    system.shutdown()
    system.awaitTermination()
  }
}
