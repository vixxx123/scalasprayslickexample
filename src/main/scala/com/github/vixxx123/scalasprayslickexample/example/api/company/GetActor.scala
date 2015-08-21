/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.github.vixxx123.scalasprayslickexample.example.api.company

import akka.actor.{Props, Actor}
import com.github.vixxx123.scalasprayslickexample.logger.Logging
import com.github.vixxx123.scalasprayslickexample.websocket.PublishWebSocket
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

case class GetMessage(ctx: RequestContext, userId: Option[Int])

/**
 * Actor handling person get message
 */
class GetActor(companyDao: CompanyDao) extends Actor with Logging with PublishWebSocket {

  override val logTag: String = getClass.getName

  override def receive: Receive = {

    // get all persons
    case GetMessage(ctx, None) =>
      L.info("Getting all companies")
      ctx.complete(companyDao.getAll)


    // get person by id
    case GetMessage(ctx, Some(id)) =>
      L.info(s"Getting company id = $id")
      val localCtx = ctx
      localCtx.complete(companyDao.getById(id))
  }
}

object GetActor {
  val Name = s"${ResourceName}GetRouter"
  def props(companyDao: CompanyDao) = Props(classOf[GetActor], companyDao)
}
