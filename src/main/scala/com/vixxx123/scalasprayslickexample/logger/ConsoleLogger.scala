/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.logger

class ConsoleLogger extends BaseLogger {

  override def debug(msg: String, tag: String): Unit = {
    println(s"DEBUG | $tag | $msg")
  }

  override def error(msg: String, tag: String, cause: Throwable, stack: Array[StackTraceElement]): Unit = {
    println(s"ERROR | $tag | MSG   | $msg")
    println(s"ERROR | $tag | CAUSE | $cause")
    stack.foreach {
      stackElement =>
        println(s"ERROR | $tag | STACK | $stackElement")
    }
  }

  override def info(msg: String, tag: String): Unit = {
    println(s"INFO | $tag | $msg")
  }
}
