package com.vixxx123.scalasprayslickexample.exampleapi.company

import akka.actor.{Props, Actor}
import com.vixxx123.scalasprayslickexample.entity.EntityHelper
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.HttpRequestHelper
import com.vixxx123.scalasprayslickexample.websocket.{CreatePublishMessage, PublishWebSocket}
import spray.httpx.SprayJsonSupport._
import spray.routing.RequestContext

case class CreateMessage(ctx: RequestContext, person: Company)

/**
 * Actor handling person create message
 */
class CreateActor(companyDb: CompanyDb) extends Actor with Logging with PublishWebSocket with HttpRequestHelper with EntityHelper  {

  override val logTag = getClass.getName

  override def receive: Receive = {

    case CreateMessage(ctx, company) =>
      try {
        val added = company.copy(id = Some(companyDb.create(company)))
        ctx.complete(added)
        publishAll(CreatePublishMessage(ResourceName, entityUri(getRequestUri(ctx), added), added))
        L.debug(s"Company create success")
      } catch {
        case e: Exception =>
          L.error(s"Ups cannot create company: ${e.getMessage}", e)
          ctx.complete(e)
      }
  }
}

object CreateActor {
  val Name = s"${ResourceName}CreateRouter"
  def props(companyDb: CompanyDb) = Props(classOf[CreateActor], companyDb)
}
