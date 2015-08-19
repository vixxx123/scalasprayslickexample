/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.example.api.company

import akka.actor.{Props, Actor}
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.EntityNotFound
import com.vixxx123.scalasprayslickexample.websocket.{DeletePublishMessage, PublishWebSocket}
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

case class DeleteMessage(ctx: RequestContext, companyId: Int)

/**
 * Actor handling delete message
 */
class DeleteActor(companyDao: CompanyDao) extends Actor with PublishWebSocket with Logging {

  override def receive: Receive = {
    case DeleteMessage(ctx, companyId) =>
      L.debug(s"deleting company $companyId")
      val count = companyDao.deleteById(companyId)
      if (count == 1) {
        ctx.complete("")
      } else {
        ctx.complete(new EntityNotFound("Trying to delete non existent entity"))
      }
      publishAll(DeletePublishMessage(ResourceName, companyId))

  }

  override val logTag: String = getClass.getName
}

object DeleteActor {
  val Name = s"${ResourceName}DeleteRouter"
  def props(companyDao: CompanyDao) = Props(classOf[DeleteActor], companyDao)
}
