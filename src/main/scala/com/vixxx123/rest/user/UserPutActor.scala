package com.vixxx123.rest.user

import akka.actor.Actor
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._

case class PutMessage(ctx: RequestContext, user: User)

class UserPutActor extends Actor {

  override def receive: Receive = {
    case PutMessage(ctx, user) =>
      val localCtx = ctx
      localCtx.complete(user)

  }
}
