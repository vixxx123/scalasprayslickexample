package com.vixxx123.logger

import akka.actor.{Props, ActorSystem, Actor}
import com.vixxx123.rest.Api

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

sealed trait LogType
case object Debug extends LogType
case object Info extends LogType
case object Error extends LogType

class LogMessage(val msg: String, val tag: String, logType: LogType)

case class Debug(override val msg: String, override val tag: String) extends LogMessage(msg, tag, Debug)
case class Info(override val msg: String, override val tag: String) extends LogMessage(msg, tag, Info)
case class Error(override val msg: String, override val tag: String, cause: Throwable, stack: Array[StackTraceElement])
  extends LogMessage(msg, tag, Error)
