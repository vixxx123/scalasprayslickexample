package com.vixxx123.rest

import akka.actor._
import com.vixxx123.rest.internal.logging.{Logging, ConsoleLogger, Logger}
import com.vixxx123.rest.user.UserApi

import spray.routing._
import spray.util.LoggingContext

class ApiActor extends Actor with Api with Logging {

  override val logTag: String = getClass.getName

  override implicit def actorRefFactory: ActorContext = context

  implicit def myExceptionHandler(implicit log: LoggingContext): ExceptionHandler = ExceptionHandler {

    case e: RestException =>
      ctx => {
        L.debug(s"Cannot respond - error: ${e.getMessage}, to request: $ctx")
        ctx.complete(e.code, e.getMessage)
      }

    case e: Exception =>
      ctx => {
        L.error("Unhandled exception: " + e.getMessage)
        ctx.complete(500, e.getMessage)
      }
  }

  override def receive = runRoute(handleExceptions(myExceptionHandler){routing})


}

object Api {
  val actorSystem = ActorSystem("restActorSystem")
  actorSystem.actorOf(Props(classOf[Logger], List(new ConsoleLogger)), Logger.LoggerActorName)
}

trait Api extends HttpService with UserApi {
  val routing = userRoute
}
