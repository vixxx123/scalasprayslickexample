package com.vixxx123.rest.person

import akka.actor.Actor
import com.vixxx123.logger.Logging
import com.vixxx123.rest.EntityNotFound
import com.vixxx123.database.DatabaseAccess
import com.vixxx123.util.SqlUtil
import com.vixxx123.websocket.{UpdatePublishMessage, PublishWebSocket}
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.{StaticQuery => Q}
import Q.interpolation

case class PutMessage(ctx: RequestContext, user: Person)
case class PatchMessage(ctx: RequestContext, userId: Int)

/**
 * Actor handling update messages
 */
class UpdateActor extends Actor with DatabaseAccess with PublishWebSocket with Logging {


  override def receive: Receive = {

    //handling put message
    case PutMessage(ctx, user) =>
      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          val updated = Persons.filter(_.id === user.id).update(user)
          if (updated == 1) {
            localCtx.complete(user)
            publishAll(UpdatePublishMessage(TableName, localCtx.request.uri.toString(), user))
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
            val user = Persons.filter(_.id === userId).firstOption
            localCtx.complete(user)
            publishAll(UpdatePublishMessage(TableName, localCtx.request.uri.toString(), user))
          } else {
            localCtx.complete(EntityNotFound(s"Not found person id $userId"))
          }
      }
  }

  override val logTag: String = getClass.getName
}
