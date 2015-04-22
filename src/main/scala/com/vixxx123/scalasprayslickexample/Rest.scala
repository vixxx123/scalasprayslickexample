package com.vixxx123.scalasprayslickexample

import akka.actor.{PoisonPill, ActorSystem}
import akka.io.IO
import akka.util.Timeout
import com.vixxx123.scalasprayslickexample.logger.{ConsoleLogger, Logger}
import com.vixxx123.scalasprayslickexample.rest.ApiService
import com.vixxx123.scalasprayslickexample.rest.company.CompanyApi
import com.vixxx123.scalasprayslickexample.rest.person.PersonApi
import com.vixxx123.scalasprayslickexample.websocket.WebSocketServer
import spray.can.Http
import spray.can.server.UHttp
import scala.concurrent.duration._

/**
  * Created by Wiktor Tychulski on 2014-11-16.
  */
object Rest extends App {
   // we need an ActorSystem to host our application in
   implicit val system = ActorSystem("on-spray-can")

   // start up logger actor system and logger actor
   Logger.LoggingActorSystem.actorOf(Logger.props(List(new ConsoleLogger)), Logger.LoggerActorName)

   // start up API service actor
   val service = system.actorOf(ApiService.props(List(PersonApi, CompanyApi)), ApiService.ActorName)
   val server = system.actorOf(WebSocketServer.props(), "websocket")

   implicit val timeout = Timeout(5.seconds)
   // start a new HTTP server on port 8080 with our service actor as the handler
   IO(UHttp) ! Http.Bind(server, "localhost", port = 8082)

   // SPRAY WORKAROUND: Must me killed before starting rest server because of actor naming collision
   system.actorSelection("/user/IO-HTTP") ! PoisonPill
   Thread.sleep(1000)
   IO(Http) ! Http.Bind(service, interface = "localhost", port = 8080)

 }
