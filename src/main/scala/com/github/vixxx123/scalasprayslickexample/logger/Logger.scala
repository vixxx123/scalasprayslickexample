/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.github.vixxx123.scalasprayslickexample.logger

/**
 * Base class for logger. Defines basic logger methods
 */
trait Logger {
  def debug(msg: String, tag: String)
  def error(msg: String, tag: String, cause: Throwable, stack: Array[StackTraceElement] = null)
  def info(msg: String, tag: String)
}
