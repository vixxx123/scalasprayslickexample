/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.logger

import java.text.SimpleDateFormat
import java.util.{TimeZone, Date}

/**
 * Console logger implementation
 */
class ConsoleLogger extends Logger {

  private val dateFormatter: SimpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
//  dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"))

  override def debug(msg: String, tag: String): Unit = {
    println(s"DEBUG | ${dateFormatter.format(new Date())} | $tag | $msg")
  }

  override def error(msg: String, tag: String, cause: Throwable, stack: Array[StackTraceElement]): Unit = {
    println(s"ERROR | ${dateFormatter.format(new Date())} | $tag | MSG   | $msg")
    println(s"ERROR | ${dateFormatter.format(new Date())} | $tag | CAUSE | $cause")
    stack.foreach {
      stackElement =>
        println(s"ERROR | $tag | STACK | $stackElement")
    }
  }

  override def info(msg: String, tag: String): Unit = {
    println(s"INFO | ${dateFormatter.format(new Date())} | $tag | $msg")
  }
}
