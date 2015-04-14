package com.vixxx123.rest.person

import akka.actor.Actor
import com.vixxx123.rest.EntityNotFound
import com.vixxx123.rest.internal.configuration.DatabaseAccess
import com.vixxx123.util.{SqlUtil, JsonUtil}
import spray.json.JsonReader
import spray.routing.{Rejection, RequestContext}
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.{GetResult, StaticQuery => Q}
import Q.interpolation

case class PutMessage(ctx: RequestContext, user: Person)
case class PatchMessage(ctx: RequestContext, userId: Int)

/**
 * Actor handling update messages
 */
class PersonUpdateActor extends Actor with DatabaseAccess {


  override def receive: Receive = {

    //handling put message
    case PutMessage(ctx, user) =>
      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          val updated = Persons.filter(_.id === user.id).update(user)
          if (updated == 1) {
            localCtx.complete(user)
          } else {
            localCtx.complete(EntityNotFound(s"Not found person id ${user.id}"))
          }
      }

    //handling patch message
    case PatchMessage(ctx, userId) =>
      val localCtx = ctx
      val updateStatement = SqlUtil.patch2updateStatement(TableName, ctx.request.message.entity.asString)

      connectionPool withSession {
        implicit session =>
          val updated = Q.updateNA(s"$updateStatement  ${SqlUtil.whereById(userId)}")
          if (updated.first == 1) {
            localCtx.complete(Persons.filter(_.id === userId).firstOption)
          } else {
            localCtx.complete(EntityNotFound(s"Not found person id $userId"))
          }
      }
  }


}
