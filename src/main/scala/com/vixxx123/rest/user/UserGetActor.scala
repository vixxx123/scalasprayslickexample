package com.vixxx123.rest.user

import akka.actor.Actor
import akka.actor.Actor.Receive
import spray.routing.{Directives, RequestContext}
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

/**
 * Created by Wiktor Tychulski on 2014-11-18.
 */

case class GetMessage(ctx: RequestContext)

class UserGetActor extends Actor {

  implicit val PersonFormat = jsonFormat2(User)

  override def receive: Receive = {
    case GetMessage(ctx) =>

        ctx.complete(User("jan", "jan123"))

  }
}
