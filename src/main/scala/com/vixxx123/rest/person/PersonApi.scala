package com.vixxx123.rest.person

import akka.actor.Props
import akka.routing.RoundRobinPool
import com.vixxx123.rest.internal.configuration.DatabaseAccess
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
trait PersonApi extends HttpService with BaseResourceApi with DatabaseAccess {

  val personCreateHandler = actorRefFactory.actorOf(RoundRobinPool(2).props(Props[PersonCreateActor]), "personCreateRouter")
  val personPutHandler = actorRefFactory.actorOf(RoundRobinPool(5).props(Props[PersonUpdateActor]), "personPutRouter")
  val personGetHandler = actorRefFactory.actorOf(RoundRobinPool(20).props(Props[PersonGetActor]), "personGetRouter")
  val personDeleteHandler = actorRefFactory.actorOf(RoundRobinPool(20).props(Props[PersonDeleteActor]), "personDeleteRouter")


  override def init() = {
    connectionPool withSession {
      implicit session =>
        if (MTable.getTables(TableName).list.isEmpty) {
          Persons.ddl.create
        }
    }
  }

  val userRoute =
    pathPrefix("person") {
      pathEnd {
        get {
          ctx => personGetHandler ! GetMessage(ctx, None)
        } ~
        post {
          entity(as[Person]) {
            user =>
              ctx => personCreateHandler ! CreateMessage(ctx, user)
          }
        }
      } ~
      pathPrefix (IntNumber){
        userId => {
          pathEnd {
            get {
              ctx => personGetHandler ! GetMessage(ctx, Some(userId))
            } ~ put {
              entity(as[Person]) { user =>
                ctx => personPutHandler ! PutMessage(ctx, user.copy(id = Some(userId)))
              }
            } ~ delete {
              ctx => personDeleteHandler ! DeleteMessage(ctx, userId)
            } ~ patch {
              ctx => personPutHandler ! PatchMessage(ctx, userId)
            }
          }
        }
      }
    }
}
