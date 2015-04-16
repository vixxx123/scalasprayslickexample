package com.vixxx123.scalasprayslickexample.rest.person

import akka.actor.Actor
import com.vixxx123.scalasprayslickexample.database.DatabaseAccess
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.websocket._
import spray.routing.RequestContext
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import scala.slick.driver.MySQLDriver.simple._

case class GetMessage(ctx: RequestContext, userId: Option[Int])

/**
 * Actor handling person get message
 */
class GetActor extends Actor with DatabaseAccess with Logging with PublishWebSocket {

  override val logTag: String = getClass.getName

  override def receive: Receive = {

    // get all persons
    case GetMessage(ctx, None) =>
      L.info("Getting all persons")

      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          localCtx.complete(Persons.list)
      }

    // get person by id
    case GetMessage(ctx, Some(userId)) =>
      L.info(s"Getting person id = $userId")
      val localCtx = ctx
      connectionPool withSession {
        implicit session =>
          localCtx.complete(Persons.filter(_.id === userId).firstOption)


      }
  }


}
