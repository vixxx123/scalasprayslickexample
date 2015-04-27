package com.vixxx123.scalasprayslickexample.exampleapi.person

import akka.actor.{Props, Actor}
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.websocket.{DeletePublishMessage, PublishWebSocket}
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._

case class DeleteMessage(ctx: RequestContext, personId: Int)

case class DeleteResult(deleted: Boolean)

/**
 * Actor handling delete message
 */
class DeleteActor(personDao: PersonDao) extends Actor with PublishWebSocket with Logging {

  override def receive: Receive = {
    case DeleteMessage(ctx, personId) =>
      L.debug(s"deleting person $personId")
      val count = personDao.deleteById(personId)
      ctx.complete(DeleteResult(count == 1))
      publishAll(DeletePublishMessage(ResourceName, personId))

  }

  override val logTag: String = getClass.getName
}

object DeleteActor {
  val Name = s"${ResourceName}DeleteRouter"
  def props(personDao: PersonDao) = Props(classOf[DeleteActor], personDao)
}
