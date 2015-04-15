package com.vixxx123.rest.person

import akka.actor.Actor
import com.vixxx123.database.DatabaseAccess
import com.vixxx123.logger.Logging
import com.vixxx123.websocket.{CreatePublishMessage, PublishWebSocket}
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._

case class CreateMessage(ctx: RequestContext, person: Person)

/**
 * Actor handling person create message
 */
class CreateActor extends Actor with DatabaseAccess with Logging with PublishWebSocket {

  override val logTag = getClass.getName

  override def receive: Receive = {

    case CreateMessage(ctx, person) =>

      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          try {
            val resId = PersonsIdReturning += person
            val addedPerson = person.copy(id = Some(resId.asInstanceOf[Int]))
            localCtx.complete(addedPerson)
            publishAll(CreatePublishMessage(TableName, localCtx.request.uri + "/" + addedPerson.id.get, addedPerson))
            L.debug(s"Person create success")
          } catch {
            case e: Exception =>
              L.error(s"Ups cannot create person: ${e.getMessage}", e)
              localCtx.complete(e)
          }
      }
  }
}
