/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.logger

trait BaseLogger {
  def debug(msg: String, tag: String)
  def error(msg: String, tag: String, cause: Throwable, stack: Array[StackTraceElement] = null)
  def info(msg: String, tag: String)
}
