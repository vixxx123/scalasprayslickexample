package com.vixxx123.scalasprayslickexample.exampleapi.company

import akka.actor.{Props, Actor}
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.{HttpRequestHelper, EntityNotFound}
import com.vixxx123.scalasprayslickexample.util.SqlUtil
import com.vixxx123.scalasprayslickexample.websocket.{PublishWebSocket, UpdatePublishMessage}
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

case class PutMessage(ctx: RequestContext, company: Company)
case class PatchMessage(ctx: RequestContext, companyId: Int)

/**
 * Actor handling update messages
 */
class UpdateActor(companyDao: CompanyDao) extends Actor with PublishWebSocket with Logging with HttpRequestHelper{


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


    // handling patch message - shitty implementation - don't use it at home :)
    // it should use json notation
    // for reference https://tools.ietf.org/html/rfc6902
    case PatchMessage(ctx, id) =>
      val localCtx = ctx
      val updateStatement = s"${SqlUtil.patch2updateStatement(companyDao.tableName, getEntityDataAsString(ctx))} ${SqlUtil.whereById(id)}"
      val updated = companyDao.runQuery(updateStatement)
      if (updated == 1) {
        val company = companyDao.getById(id)
        localCtx.complete(company)
        publishAll(UpdatePublishMessage(ResourceName, getRequestUri(ctx), company))
      } else {
        ctx.complete(EntityNotFound(s"Not found company id $id"))
      }
  }

  override val logTag: String = getClass.getName
}

object UpdateActor {
  val Name = s"${ResourceName}PutRouter"
  def props(companyDao: CompanyDao) = Props(classOf[UpdateActor], companyDao)
}
