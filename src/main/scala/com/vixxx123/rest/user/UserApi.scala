package com.vixxx123.rest.user

import akka.actor.Props
import akka.routing.RoundRobinPool
import com.vixxx123.rest.Api
import spray.routing.HttpService
import spray.httpx.SprayJsonSupport._

trait UserApi extends HttpService {

  val userCreateHandler = Api.actorSystem.actorOf(RoundRobinPool(2).props(Props[UserCreateActor]), "userCreateRouter")
  val userPutHandler = Api.actorSystem.actorOf(RoundRobinPool(5).props(Props[UserPutActor]), "userPutRouter")
  val userGetHandler = Api.actorSystem.actorOf(RoundRobinPool(20).props(Props[UserGetActor]), "userGetRouter")

  val userRoute =
    pathPrefix("user") {
      pathEnd {
        get {
          ctx => userGetHandler ! GetMessage(ctx, None)
        } ~
        post {
          entity(as[User]) {
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
              entity(as[User]) { user =>
                ctx => userPutHandler ! PutMessage(ctx, user)
              }
            }
          }
        }
      }
    }
}
