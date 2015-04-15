package com.vixxx123.rest.company

import akka.actor.Actor
import com.vixxx123.database.DatabaseAccess
import com.vixxx123.logger.Logging
import com.vixxx123.websocket.{DeletePublishMessage, PublishWebSocket}
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

import scala.slick.driver.MySQLDriver.simple._

case class DeleteMessage(ctx: RequestContext, companyId: Int)

case class DeleteResult(deleted: Boolean)

/**
 * Actor handling delete message
 */
class DeleteActor extends Actor with DatabaseAccess with PublishWebSocket with Logging {

  override def receive: Receive = {
    case DeleteMessage(ctx, companyId) =>
      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          val deleted = Companies.filter(_.id === companyId).delete
          localCtx.complete(DeleteResult(deleted == 1))
          publishAll(DeletePublishMessage(TableName, companyId))
      }

  }

  override val logTag: String = getClass.getName
}

