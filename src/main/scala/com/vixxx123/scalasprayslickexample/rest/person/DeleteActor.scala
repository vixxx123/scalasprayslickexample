package com.vixxx123.scalasprayslickexample.rest.person

import akka.actor.Actor
import com.vixxx123.scalasprayslickexample.database.DatabaseAccess
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.websocket.{DeletePublishMessage, PublishWebSocket}
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._

case class DeleteMessage(ctx: RequestContext, personId: Int)

case class DeleteResult(deleted: Boolean)

/**
 * Actor handling delete message
 */
class DeleteActor extends Actor with DatabaseAccess with PublishWebSocket with Logging {

  override def receive: Receive = {
    case DeleteMessage(ctx, personId) =>
      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          val deleted = Persons.filter(_.id === personId).delete
          localCtx.complete(DeleteResult(deleted == 1))
          publishAll(DeletePublishMessage(TableName, personId))
      }

  }

  override val logTag: String = getClass.getName
}

