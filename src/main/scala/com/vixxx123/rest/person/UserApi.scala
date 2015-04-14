package com.vixxx123.rest.person

import akka.actor.Props
import akka.routing.RoundRobinPool
import com.vixxx123.rest.internal.configuration.DatabaseAccess
import com.vixxx123.rest.{BaseResourceApi, Api}
import spray.routing.HttpService
import spray.httpx.SprayJsonSupport._
import scala.slick.driver.MySQLDriver.simple._

import scala.slick.jdbc.meta.MTable

trait UserApi extends HttpService with BaseResourceApi with DatabaseAccess {

  val userCreateHandler = actorRefFactory.actorOf(RoundRobinPool(2).props(Props[UserCreateActor]), "userCreateRouter")
  val userPutHandler = actorRefFactory.actorOf(RoundRobinPool(5).props(Props[UserPutActor]), "userPutRouter")
  val userGetHandler = actorRefFactory.actorOf(RoundRobinPool(20).props(Props[UserGetActor]), "userGetRouter")
  val userDeleteHandler = actorRefFactory.actorOf(RoundRobinPool(20).props(Props[UserDeleteActor]), "userDeleteRouter")


  override def init() = {
    connectionPool withSession {
      implicit session =>
        if (MTable.getTables(TableName).list.isEmpty) {
          Users.ddl.create
        }
    }
  }

  val userRoute =
    pathPrefix("person") {
      pathEnd {
        get {
          ctx => userGetHandler ! GetMessage(ctx, None)
        } ~
        post {
          entity(as[Person]) {
            user =>
              ctx => userCreateHandler ! CreateMessage(ctx, user)
          }
        }
      } ~
      pathPrefix (IntNumber){
        userId => {
          pathEnd {
            get {
              ctx => userGetHandler ! GetMessage(ctx, Some(userId))
            } ~ put {
              entity(as[Person]) { user =>
                ctx => userPutHandler ! PutMessage(ctx, user.copy(id = Some(userId)))
              }
            } ~ delete {
              ctx => userDeleteHandler ! DeleteMessage(ctx, userId)
            } ~ patch {
              ctx => userPutHandler ! PatchMessage(ctx, userId)
            }
          }
        }
      }
    }
}