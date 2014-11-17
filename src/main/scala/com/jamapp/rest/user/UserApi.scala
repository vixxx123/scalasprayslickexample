package com.jamapp.rest.user

import spray.json.DefaultJsonProtocol
import spray.routing.HttpService
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

/**
 * Created by Wiktor Tychulski on 2014-11-16.
 */
trait UserApi extends HttpService {

  case class User(name: String, login: String)


  implicit val PersonFormat = jsonFormat2(User)


  val userRoute =
    pathPrefix("user") {
      pathEnd {
        get {
            complete( Map("users" -> List(User("jan", "jan123"), User("luki", "luki123"))))
        } ~
        post {
          entity(as[User]) { user =>
            complete(user)
          }
        }
      } ~
      pathPrefix (IntNumber){
        userId => {
          pathEnd {
            get {
              complete(User("jan", "jan123"))
            }
          }
        }
      }
    }
}
