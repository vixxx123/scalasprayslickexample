package com.vixxx123.rest.user

import akka.actor.Actor
import com.vixxx123.rest.internal.configuration.DatabaseAccess
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._

case class DeleteMessage(ctx: RequestContext, userId: Int)

case class DeleteResult(deleted: Boolean)

class UserDeleteActor extends Actor with DatabaseAccess {

  override def receive: Receive = {
    case DeleteMessage(ctx, userId) =>
      val localCtx = ctx
      databasePool withSession {
        implicit session =>
          val deleted = Users.filter(_.id === userId).delete
          localCtx.complete(DeleteResult(deleted == 1))
      }

  }
}

