package com.vixxx123.scalasprayslickexample.rest.company

import akka.actor.Actor
import com.vixxx123.scalasprayslickexample.database.DatabaseAccess
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.websocket.{CreatePublishMessage, PublishWebSocket}
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

case class CreateMessage(ctx: RequestContext, person: Company)

/**
 * Actor handling person create message
 */
class CreateActor extends Actor with DatabaseAccess with Logging with PublishWebSocket {

  override val logTag = getClass.getName

  override def receive: Receive = {

    case CreateMessage(ctx, company) =>

      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          try {
            val resId = CompaniesIdReturning += company
            val added = company.copy(id = Some(resId.asInstanceOf[Int]))
            localCtx.complete(added)
            publishAll(CreatePublishMessage(TableName, localCtx.request.uri + "/" + added.id.get, added))
            L.debug(s"Company create success")
          } catch {
            case e: Exception =>
              L.error(s"Ups cannot create company: ${e.getMessage}", e)
              localCtx.complete(e)
          }
      }
  }
}
