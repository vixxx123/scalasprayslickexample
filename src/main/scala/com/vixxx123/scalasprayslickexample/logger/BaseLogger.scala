package com.vixxx123.scalasprayslickexample.logger

trait BaseLogger {
  def debug(msg: String, tag: String)
  def error(msg: String, tag: String, cause: Throwable, stack: Array[StackTraceElement] = null)
  def info(msg: String, tag: String)
}
