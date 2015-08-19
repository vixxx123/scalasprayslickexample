/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.logger

import akka.actor.{ActorRef, Props, ActorSystem, Actor}

/**
 * Logging service 
 * 
 * @param loggers - list of loggers
 */
class LoggingService(loggers: List[Logger]) extends Actor {

  override def receive: Receive = {
    case Debug(msg, tag) =>
         loggers.foreach{ _.debug(msg, tag) }

    case Info(msg, tag) =>
      loggers.foreach{ _.info(msg, tag)}

    case Error(msg, tag, cause, stack) =>
      loggers.foreach{ _.error(msg, tag, cause, stack)}

    case any: Any =>
      println("Log type message unknown")

  }
}

object LoggingService {

  private var loggingService: ActorRef = null
  val LoggerActorName = "Logger-Actor"

  private def props(handlers: List[Logger]) = Props(classOf[LoggingService], handlers)

  def getLoggingService: ActorRef = {
    loggingService
  }

  def init(loggers: List[Logger])(implicit actorSystem: ActorSystem): Unit = {
    loggingService = actorSystem.actorOf(LoggingService.props(loggers), LoggingService.LoggerActorName)
  }

}




