package com.jamapp.rest

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorRefFactory}
import spray.http.MediaTypes
import spray.routing.HttpService

/**
 * Created by wiktort on 2014-11-13.
 */
class ApiActor extends Actor with Api {

  override implicit def actorRefFactory: ActorRefFactory = context

  override def receive: Receive = runRoute(routing)
}


trait Api extends HttpService {
  val routing = null
}

trait DummyRoute extends HttpService {
  val dummyRoute = path("/dummy"){
     get{
       respondWithMediaType(MediaTypes.`application/json`) {
         complete("""{"res": "OK"}""")
       }
     }
  }
}