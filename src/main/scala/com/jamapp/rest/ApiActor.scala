package com.jamapp.rest

import akka.actor.{Actor, ActorRefFactory}
import spray.http.MediaTypes
import spray.routing.HttpService
import spray.http.MediaTypes._

/**
 * Created by wiktort on 2014-11-13.
 */
class ApiActor extends Actor with Api {

  override implicit def actorRefFactory: ActorRefFactory = context

  override def receive: Receive = runRoute(routing)
}


trait Api extends HttpService with DummyRoute with GeekRoute{
  val routing = dummyRoute ~ geekRoute
}

trait DummyRoute extends HttpService {
  val dummyRoute =
    pathPrefix("dummy") {
      pathEnd {
        get {
          respondWithMediaType(`application/json`) {
            complete( """{"dammies": ["jan", "artur"]}""")
          }
        }
      }
    }
}

trait GeekRoute extends HttpService {
  val geekRoute =
    pathPrefix("geek") {
      pathEnd {
        get {
          respondWithMediaType(MediaTypes.`application/json`) {
            complete( """{"geeks": ["wiki", "tiki"]}""")
          }
        }
      }
    }
}