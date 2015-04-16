package com.vixxx123.scalasprayslickexample.logger

import akka.actor.{Props, ActorSystem, Actor}
import com.vixxx123.scalasprayslickexample.rest.Api

class Logger(handler: List[BaseLogger]) extends Actor {

  override def receive: Receive = {
    case Debug(msg, tag) =>
         handler.foreach{ _.debug(msg, tag) }

    case Info(msg, tag) =>
      handler.foreach{ _.info(msg, tag)}

    case Error(msg, tag, cause, stack) =>
      handler.foreach{ _.error(msg, tag, cause, stack)}

    case any: Any =>
      println("Log type message unknown")

  }
}

object Logger {
  val LoggingActorSystem = ActorSystem("loggingActorSystem")
  val LoggerActorName = "Logger-Actor"

  def props(handlers: List[BaseLogger]) = Props(classOf[Logger], handlers)
}




