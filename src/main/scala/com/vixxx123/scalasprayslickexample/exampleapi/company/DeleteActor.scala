package com.vixxx123.scalasprayslickexample.exampleapi.company

import akka.actor.{Props, Actor}
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.websocket.{DeletePublishMessage, PublishWebSocket}
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

case class DeleteMessage(ctx: RequestContext, companyId: Int)

case class DeleteResult(deleted: Boolean)

/**
 * Actor handling delete message
 */
class DeleteActor(companyDao: CompanyDao) extends Actor with PublishWebSocket with Logging {

  override def receive: Receive = {
    case DeleteMessage(ctx, companyId) =>
      L.debug(s"deleting company $companyId")
      val count = companyDao.deleteById(companyId)
      ctx.complete(DeleteResult(count == 1))
      publishAll(DeletePublishMessage(ResourceName, companyId))

  }

  override val logTag: String = getClass.getName
}

object DeleteActor {
  val Name = s"${ResourceName}DeleteRouter"
  def props(companyDao: CompanyDao) = Props(classOf[DeleteActor], companyDao)
}
