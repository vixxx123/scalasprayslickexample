package com.vixxx123.rest.user

import akka.actor.Actor
import com.vixxx123.rest.internal.configuration.DatabaseAccess
import com.vixxx123.rest.internal.logging.Logging
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import scala.slick.driver.MySQLDriver.simple._

case class GetMessage(ctx: RequestContext, userId: Option[Int])

class UserGetActor extends Actor with DatabaseAccess with Logging {

  override val logTag: String = getClass.getName

  override def receive: Receive = {

    case GetMessage(ctx, None) =>
      L.info("Getting all users")
      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          localCtx.complete(Users.list)
      }

    case GetMessage(ctx, Some(userId)) =>
      L.info(s"Getting user id = $userId")
      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          localCtx.complete(Users.filter(_.id === userId).firstOption)


      }
  }


}
