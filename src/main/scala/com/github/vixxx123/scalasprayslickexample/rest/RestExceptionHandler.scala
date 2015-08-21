/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.github.vixxx123.scalasprayslickexample.rest

import com.github.vixxx123.scalasprayslickexample.logger.Logging
import spray.routing.ExceptionHandler
import spray.util.LoggingContext

/**
 * Rest exception handler
 */
class RestExceptionHandler extends Logging {

  override val logTag = getClass.getName

  implicit def exceptionHandler(implicit log: LoggingContext): ExceptionHandler = ExceptionHandler {

    case e: RestException =>
      ctx => {
        L.error(s"Cannot respond - error: ${e.getMessage}, to request: $ctx", e)
        ctx.complete(e.code, e.getMessage)
      }

    case e: Exception =>
      ctx => {
        L.error("Unhandled exception: " + e.getMessage, e)
        ctx.complete(500, e.getMessage)
      }
  }

}
