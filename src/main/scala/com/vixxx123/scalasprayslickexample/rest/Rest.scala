/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.rest

import akka.actor.{ActorSystem, PoisonPill}
import akka.io.IO
import akka.util.Timeout
import com.vixxx123.scalasprayslickexample.logger.{BaseLogger, Logger}
import com.vixxx123.scalasprayslickexample.rest.oauth2.{AuthUserDao, OauthConfig, SessionService}
import com.vixxx123.scalasprayslickexample.websocket.{WebSocketServerWithAuthorization, WebSocketServer}
import spray.can.Http
import spray.can.server.UHttp

import scala.concurrent.duration._

class Rest(actorSystem: ActorSystem, listOfApis: List[Api], loggers: List[BaseLogger],
           oauthConfig: Option[OauthConfig]  = None) {
  // we need an ActorSystem to host our application in
  implicit val system = actorSystem

  def withOauth(oauthConfig: OauthConfig) = {
    new Rest(actorSystem, listOfApis, loggers, Some(oauthConfig))
  }

  def start(): Unit = {

    println("Starting up...")
    // start up logger actor system and logger actor
    Logger.LoggingActorSystem.actorOf(Logger.props(loggers), Logger.LoggerActorName)


    // start up API service actor

    val apis = oauthConfig match {
      case Some(config) =>
        SessionService.init(config.authorizationProvider)
        listOfApis :+ config.oauthApi

      case None => listOfApis
    }

    val service = system.actorOf(ApiService.props(apis), ApiService.ActorName)
    val server = system.actorOf(WebSocketServer.props(oauthConfig), WebSocketServer.Name)


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
    Logger.shutdown()
    system.shutdown()
    system.awaitTermination()
  }
}
