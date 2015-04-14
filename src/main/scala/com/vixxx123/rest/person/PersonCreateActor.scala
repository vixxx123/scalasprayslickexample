package com.vixxx123.rest.person

import akka.actor.Actor
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import com.vixxx123.rest.internal.configuration.DatabaseAccess
import com.vixxx123.rest.internal.logger.Logging
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._

case class CreateMessage(ctx: RequestContext, person: Person)

/**
 * Actor handling person create message
 */
class PersonCreateActor extends Actor with DatabaseAccess with Logging {

  override val logTag = getClass.getName

  override def receive: Receive = {
    case CreateMessage(ctx, person) =>

      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          try {
            val resId = PersonsIdReturning += person
            localCtx.complete(person.copy(id = Some(resId.asInstanceOf[Int])))
            L.debug(s"Person create success")
          } catch {
            case e: Exception =>
              L.error(s"Ups cannot create person: ${e.getMessage}", e)
              localCtx.complete(e)
          }
      }
  }
}
