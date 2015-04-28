/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.rest

import spray.routing.RequestContext

trait HttpRequestHelper {

  def getRequestUri(ctx: RequestContext): String = ctx.request.uri.toString()

  def getEntityDataAsString(ctx: RequestContext): String = ctx.request.message.entity.asString
}
