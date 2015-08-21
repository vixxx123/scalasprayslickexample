/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.github.vixxx123.scalasprayslickexample.example.api.company

import akka.actor.{Props, Actor}
import com.github.vixxx123.scalasprayslickexample.entity.JsonNotation
import com.github.vixxx123.scalasprayslickexample.logger.Logging
import com.github.vixxx123.scalasprayslickexample.rest.{EntityNotFound, UpdateException}
import com.github.vixxx123.scalasprayslickexample.util.HttpRequestContextUtils
import com.github.vixxx123.scalasprayslickexample.websocket.{UpdatePublishMessage, PublishWebSocket}
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

case class PutMessage(ctx: RequestContext, company: Company)
case class PatchMessage(ctx: RequestContext, patch: List[JsonNotation], companyId: Int)

/**
 * Actor handling update messages
 */
class UpdateActor(companyDao: CompanyDao) extends Actor with PublishWebSocket with Logging with HttpRequestContextUtils {


  override def receive: Receive = {

    //handling put message
    case PutMessage(ctx, company) =>

      val updated = companyDao.update(company)
      if (updated == 1) {
        ctx.complete(company)
        publishAll(UpdatePublishMessage(ResourceName, getRequestUri(ctx), company))
      } else {
        ctx.complete(EntityNotFound(s"Not found company id ${company.id}"))
      }

    case PatchMessage(ctx, patch, id) =>
      try {
        val updated = companyDao.patch(patch, id)
        if (updated.forall(_ == 1)) {
          val company = companyDao.getById(id)
          ctx.complete(company)
          publishAll(UpdatePublishMessage(ResourceName, getRequestUri(ctx), company))
        } else {
          ctx.complete(EntityNotFound(s"Not found company id $id"))
        }
      } catch {
        case e: UpdateException =>
          ctx.complete(e)
      }
  }

  override val logTag: String = getClass.getName
}

object UpdateActor {
  val Name = s"${ResourceName}PutRouter"
  def props(companyDao: CompanyDao) = Props(classOf[UpdateActor], companyDao)
}
