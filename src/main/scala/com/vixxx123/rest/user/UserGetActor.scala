package com.vixxx123.rest.user

import akka.actor.Actor
import com.vixxx123.rest.configuration.DatabaseAccess
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import scala.slick.driver.MySQLDriver.simple._

case class GetMessage(ctx: RequestContext, userId: Option[Int])

class UserGetActor extends Actor with DatabaseAccess {

  override def receive: Receive = {

    case GetMessage(ctx, None) =>

      val localCtx = ctx
      databasePool withSession {
        implicit session =>
          localCtx.complete(Users.list)
      }

    case GetMessage(ctx, Some(userId)) =>
      val localCtx = ctx
      databasePool withSession {
        implicit session =>
          localCtx.complete(Users.filter(_.id === userId).first)


      }
  }
}
