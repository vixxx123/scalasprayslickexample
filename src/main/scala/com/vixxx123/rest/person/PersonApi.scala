package com.vixxx123.rest.person

import akka.actor.Props
import akka.routing.RoundRobinPool
import com.vixxx123.database.DatabaseAccess
import com.vixxx123.logger.Logging
import com.vixxx123.rest.{BaseResourceApi, Api}
import spray.routing.HttpService
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._

import scala.slick.jdbc.meta.MTable

/**
 * Person API main class
 *
 * trait HttpService - for spray routing
 * trait BaseResourceApi - for initialization
 * trait DatabaseAccess - for db access
 *
 */
trait PersonApi extends HttpService with BaseResourceApi with DatabaseAccess with Logging {

  val personCreateHandler = actorRefFactory.actorOf(RoundRobinPool(2).props(Props[CreateActor]), s"${TableName}CreateRouter")
  val personPutHandler = actorRefFactory.actorOf(RoundRobinPool(5).props(Props[UpdateActor]), s"${TableName}PutRouter")
  val personGetHandler = actorRefFactory.actorOf(RoundRobinPool(20).props(Props[GetActor]), s"${TableName}GetRouter")
  val personDeleteHandler = actorRefFactory.actorOf(RoundRobinPool(20).props(Props[DeleteActor]), s"${TableName}DeleteRouter")

  override val logTag: String = getClass.getName

  override def init() = {

    connectionPool withSession {
      L.debug("initializing persons")
      implicit session =>
        if (MTable.getTables(TableName).list.isEmpty) {
          Persons.ddl.create
        }
    }
  }

  val userRoute =
    pathPrefix(TableName) {
      pathEnd {
        get {
          ctx => personGetHandler ! GetMessage(ctx, None)
        } ~
        post {
          entity(as[Person]) {
            entity =>
              ctx => personCreateHandler ! CreateMessage(ctx, entity)
          }
        }
      } ~
      pathPrefix (IntNumber){
        entityId => {
          pathEnd {
            get {
              ctx => personGetHandler ! GetMessage(ctx, Some(entityId))
            } ~ put {
              entity(as[Person]) { entity =>
                ctx => personPutHandler ! PutMessage(ctx, entity.copy(id = Some(entityId)))
              }
            } ~ delete {
              ctx => personDeleteHandler ! DeleteMessage(ctx, entityId)
            } ~ patch {
              ctx => personPutHandler ! PatchMessage(ctx, entityId)
            }
          }
        }
      }
    }
}
