package com.jamapp.rest.user

import akka.actor.{Props, ActorSystem}
import akka.routing.RoundRobinPool
import com.jamapp.rest.Api
import spray.json.DefaultJsonProtocol
import spray.routing.HttpService
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

/**
 * Created by Wiktor Tychulski on 2014-11-16.
 */

case class User(name: String, login: String)

trait UserApi extends HttpService {

  val userCreateHandler = Api.actorSystem.actorOf(RoundRobinPool(20).props(Props[UserCreateActor]), "userCreateRouter")

  implicit val PersonFormat = jsonFormat2(User)


  val userRoute =
    pathPrefix("user") {
      pathEnd {
        get {
            complete( Map("users" -> List(User("jan", "jan123"), User("luki", "luki123"))))
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
              complete(User("jan", "jan123"))
            } ~ put {
              entity(as[User]) { user =>
                complete(user)
              }
            }
          }
        }
      }
    }
}
