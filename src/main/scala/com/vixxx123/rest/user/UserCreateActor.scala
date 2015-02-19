package com.vixxx123.rest.user

import akka.actor.Actor
import com.vixxx123.rest.configuration.DatabaseAccess
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._

case class CreateMessage(ctx: RequestContext, user: User)

class UserCreateActor extends Actor with DatabaseAccess{

  override def receive: Receive = {
    case CreateMessage(ctx, user) =>

      val localCtx = ctx
      databasePool withSession {
        implicit session =>
          val resId = UsersIdReturning += user
          localCtx.complete(user.copy(id = Some(resId.asInstanceOf[Int])))
      }
  }
}
