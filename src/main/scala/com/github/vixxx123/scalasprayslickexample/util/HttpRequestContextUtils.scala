/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.github.vixxx123.scalasprayslickexample.util

import spray.routing.RequestContext

/**
 * Utils methods which helps dealing with RequestContext
 *
 */
trait HttpRequestContextUtils {

  def getRequestUri(ctx: RequestContext): String = ctx.request.uri.toString()

  def getEntityDataAsString(ctx: RequestContext): String = ctx.request.message.entity.asString
}
