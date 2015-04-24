package com.vixxx123.scalasprayslickexample.rest

import spray.routing.RequestContext

/**
 * Created by WiktorT on 24/04/2015.
 */
trait HttpRequestHelper {

  def getRequestUri(ctx: RequestContext): String = ctx.request.uri.toString()

  def getEntityDataAsString(ctx: RequestContext): String = ctx.request.message.entity.asString
}
