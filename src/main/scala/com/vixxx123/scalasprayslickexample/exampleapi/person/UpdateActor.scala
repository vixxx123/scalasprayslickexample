package com.vixxx123.scalasprayslickexample.exampleapi.person

import akka.actor.{Props, Actor}
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.{HttpRequestHelper, EntityNotFound}
import com.vixxx123.scalasprayslickexample.util.SqlUtil
import com.vixxx123.scalasprayslickexample.websocket.{UpdatePublishMessage, PublishWebSocket}
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._

case class PutMessage(ctx: RequestContext, user: Person)
case class PatchMessage(ctx: RequestContext, userId: Int)

/**
 * Actor handling update messages
 */
class UpdateActor(personDao: PersonDao) extends Actor with PublishWebSocket with Logging with HttpRequestHelper {


  override def receive: Receive = {

    //handling put message
    case PutMessage(ctx, person) =>

      val updated = personDao.update(person)
      if (updated == 1) {
        ctx.complete(person)
        publishAll(UpdatePublishMessage(ResourceName, getRequestUri(ctx), person))
      } else {
        ctx.complete(EntityNotFound(s"Not found person id ${person.id}"))
      }


    //handling patch message
    case PatchMessage(ctx, id) =>
      val localCtx = ctx
      val updateStatement = s"${SqlUtil.patch2updateStatement(personDao.tableName, getEntityDataAsString(ctx))} ${SqlUtil.whereById(id)}"
      val updated = personDao.runQuery(updateStatement)
      if (updated == 1) {
        val person = personDao.getById(id)
        localCtx.complete(person)
        publishAll(UpdatePublishMessage(ResourceName, getRequestUri(ctx), person))
      } else {
        ctx.complete(EntityNotFound(s"Not found person id $id"))
      }
  }

  override val logTag: String = getClass.getName
}

object UpdateActor {
  val Name = s"${ResourceName}PutRouter"
  def props(personDao: PersonDao) = Props(classOf[UpdateActor], personDao)
}
