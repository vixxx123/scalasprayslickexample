package com.vixxx123.scalasprayslickexample.exampleapi.person

import akka.actor.{Props, Actor}
import com.vixxx123.scalasprayslickexample.entity.EntityHelper
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.HttpRequestHelper
import com.vixxx123.scalasprayslickexample.websocket.{CreatePublishMessage, PublishWebSocket}
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._

case class CreateMessage(ctx: RequestContext, person: Person)

/**
 * Actor handling person create message
 */
class CreateActor(personDao: PersonDao) extends Actor with Logging with PublishWebSocket with HttpRequestHelper with EntityHelper {

  override val logTag = getClass.getName

  override def receive: Receive = {

    case CreateMessage(ctx, person) =>

      try {
        val addedPerson = person.copy(id = Some(personDao.create(person)))
        ctx.complete(addedPerson)
        publishAll(CreatePublishMessage(ResourceName, entityUri(getRequestUri(ctx), addedPerson), addedPerson))
        L.debug(s"Person create success")
      } catch {
        case e: Exception =>
          L.error(s"Ups cannot create person: ${e.getMessage}", e)
          ctx.complete(e)
      }
  }
}

object CreateActor {
  val Name = s"${ResourceName}CreateRouter"
  def props(personDb: PersonDao) = Props(classOf[CreateActor], personDb)
}
