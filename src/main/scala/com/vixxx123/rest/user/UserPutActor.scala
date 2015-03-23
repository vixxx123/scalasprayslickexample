package com.vixxx123.rest.user

import akka.actor.Actor
import com.vixxx123.rest.EntityNotFound
import com.vixxx123.rest.internal.configuration.DatabaseAccess
import spray.routing.{Rejection, RequestContext}
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._

case class PutMessage(ctx: RequestContext, user: User)

class UserPutActor extends Actor with DatabaseAccess {

  override def receive: Receive = {
    case PutMessage(ctx, user) =>
      val localCtx = ctx
      databasePool withSession {
        implicit session =>
          val updated = Users.filter(_.id === user.id).update(user)
          if (updated == 1) {
            localCtx.complete(user)
          } else {
            localCtx.complete(EntityNotFound("Not found user id " + user.id))
          }
      }
  }
}
