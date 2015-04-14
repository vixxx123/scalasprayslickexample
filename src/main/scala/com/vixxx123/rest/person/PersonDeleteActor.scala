package com.vixxx123.rest.person

import akka.actor.Actor
import com.vixxx123.rest.internal.configuration.DatabaseAccess
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._

case class DeleteMessage(ctx: RequestContext, personId: Int)

case class DeleteResult(deleted: Boolean)

/**
 * Actor handling delete message
 */
class PersonDeleteActor extends Actor with DatabaseAccess {

  override def receive: Receive = {
    case DeleteMessage(ctx, personId) =>
      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          val deleted = Persons.filter(_.id === personId).delete
          localCtx.complete(DeleteResult(deleted == 1))
      }

  }
}

