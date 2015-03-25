package com.vixxx123.rest.user

import akka.actor.Actor
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
import com.vixxx123.rest.internal.configuration.DatabaseAccess
import com.vixxx123.rest.internal.logging.Logging
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._

case class CreateMessage(ctx: RequestContext, user: User)

class UserCreateActor extends Actor with DatabaseAccess with Logging {

  override val logTag = getClass.getName

  override def receive: Receive = {
    case CreateMessage(ctx, user) =>

      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          try {
            val resId = UsersIdReturning += user
            localCtx.complete(user.copy(id = Some(resId.asInstanceOf[Int])))
            L.debug(s"User create success")
          } catch {
            case e: MySQLIntegrityConstraintViolationException =>
              L.info(s"Login already exists, ${user.login}")
              localCtx.complete(new UserAlreadyExists(s"Login is already taken - ${user.login}"))
            case e: Exception =>
              L.error(s"Ups cannot create user: ${e.getMessage}", e)
              localCtx.complete(e)
          }
      }
  }
}
