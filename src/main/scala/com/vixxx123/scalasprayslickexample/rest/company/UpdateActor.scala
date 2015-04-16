package com.vixxx123.scalasprayslickexample.rest.company

import akka.actor.Actor
import com.vixxx123.scalasprayslickexample.database.DatabaseAccess
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.EntityNotFound
import com.vixxx123.scalasprayslickexample.util.SqlUtil
import com.vixxx123.scalasprayslickexample.websocket.{PublishWebSocket, UpdatePublishMessage}
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.{StaticQuery => Q}

case class PutMessage(ctx: RequestContext, company: Company)
case class PatchMessage(ctx: RequestContext, companyId: Int)

/**
 * Actor handling update messages
 */
class UpdateActor extends Actor with DatabaseAccess with PublishWebSocket with Logging {


  override def receive: Receive = {

    //handling put message
    case PutMessage(ctx, company) =>
      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          val updated = Companies.filter(_.id === company.id).update(company)
          if (updated == 1) {
            localCtx.complete(company)
            publishAll(UpdatePublishMessage(TableName, localCtx.request.uri.toString(), company))
          } else {
            localCtx.complete(EntityNotFound(s"Not found company id ${company.id}"))
          }
      }

    //handling patch message
    case PatchMessage(ctx, companyId) =>
      val localCtx = ctx
      val updateStatement = SqlUtil.patch2updateStatement(TableName, ctx.request.message.entity.asString)

      connectionPool withSession {
        implicit session =>
          val updated = Q.updateNA(s"$updateStatement  ${SqlUtil.whereById(companyId)}")
          if (updated.first == 1) {
            val user = Companies.filter(_.id === companyId).firstOption
            localCtx.complete(user)
            publishAll(UpdatePublishMessage(TableName, localCtx.request.uri.toString(), user))
          } else {
            localCtx.complete(EntityNotFound(s"Not found company id companyId"))
          }
      }
  }

  override val logTag: String = getClass.getName
}
