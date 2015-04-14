package com.vixxx123.rest

import com.vixxx123.rest.internal.logger.Logging
import spray.routing.ExceptionHandler
import spray.util.LoggingContext

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