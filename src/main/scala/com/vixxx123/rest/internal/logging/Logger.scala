package com.vixxx123.rest.internal.logging

import akka.actor.Actor
import com.vixxx123.rest.Api

class
Logger(handler: List[LoggerHandler]) extends Actor {

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
  val LoggerActorName = "Logger-Actor"
}

trait Logging {

  val logger = Api.actorSystem.actorSelection(s"/user/${Logger.LoggerActorName}")
  val logTag: String


  object L {

    def debug(msg: String) = {
      logger ! Debug(msg, logTag)
    }

    def debug(msg: String, logTag: String) = {
      logger ! Debug(msg, logTag)
    }

    def info(msg: String) = {
      logger ! Info(msg, logTag)
    }

    def info(msg: String, logTag: String) = {
      logger ! Info(msg, logTag)
    }

    def error(msg: String, cause: Throwable = null, stack: Array[StackTraceElement] = null) = {
      logger ! Error(msg, logTag, cause, stack)
    }

    def error(msg: String, logTag: String, cause: Throwable, stack: Array[StackTraceElement]) = {
      logger ! Error(msg, logTag, cause, stack)
    }
  }
}

trait LoggerHandler {
  def debug(msg: String, tag: String)
  def error(msg: String, tag: String, cause: Throwable, stack: Array[StackTraceElement] = null)
  def info(msg: String, tag: String)
}

class ConsoleLogger extends LoggerHandler {

  override def debug(msg: String, tag: String): Unit = {
    println(s"DEBUG | $tag | $msg")
  }

  override def error(msg: String, tag: String, cause: Throwable, stack: Array[StackTraceElement]): Unit = {
    println(s"ERROR | $tag | $msg")
    println(s"ERROR | $tag | $stack")
  }

  override def info(msg: String, tag: String): Unit = {
    println(s"INFO | $tag | $msg")
  }
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
