/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.github.vixxx123.scalasprayslickexample.logger

trait Logging {

  private lazy val logger = LoggingService.getLoggingService
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

    def error(msg: String, e: Exception) = {
      logger ! Error(msg, logTag, e.getCause, e.getStackTrace)
    }

    def error(msg: String, logTag: String, e: Exception) = {
      logger ! Error(msg, logTag, e.getCause, e.getStackTrace)
    }
  }
}
