/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.github.vixxx123.scalasprayslickexample.logger

/**
 * Log messages
 *
 * @param msg - message to be logged
 * @param tag - tag
 * @param logType - type of message
 */
private[logger] sealed class LogMessage(val msg: String, val tag: String, logType: LogLevel)

case class Debug(override val msg: String, override val tag: String) extends LogMessage(msg, tag, DebugLevel)
case class Info(override val msg: String, override val tag: String) extends LogMessage(msg, tag, InfoLevel)
case class Error(override val msg: String, override val tag: String, cause: Throwable, stack: Array[StackTraceElement])
  extends LogMessage(msg, tag, ErrorLevel)