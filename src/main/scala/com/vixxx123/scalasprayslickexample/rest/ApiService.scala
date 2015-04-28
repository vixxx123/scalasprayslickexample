package com.vixxx123.scalasprayslickexample.rest

import akka.actor._
import com.vixxx123.scalasprayslickexample.entity.JsonNotation
import com.vixxx123.scalasprayslickexample.logger.Logging
import spray.routing._
import spray.json.DefaultJsonProtocol._

/**
 * Main Api service class
 */
class ApiService(availableApis: List[Api]) extends Actor with HttpServiceBase with Logging {

  val apis = availableApis.map{_.create(context)}
  apis.foreach(_.init())

  val routing: Route = apis.foldLeft[Route](null)((a,b) => if (a == null) b.route() else {a ~ b.route()})

  override val logTag: String = getClass.getName

  override def receive = runRoute(handleExceptions(new RestExceptionHandler().exceptionHandler){routing})

}

object ApiService {
  val ActorName = "api-root"

  def props(availableApis: List[Api]) = Props(classOf[ApiService], availableApis)
}


trait Api {
  def create(actorContext: ActorContext): BaseResourceApi
}

trait BaseResourceApi extends HttpServiceBase{

  implicit val JsonNotationFormat = jsonFormat3(JsonNotation)

  def init(): Unit = {}
  def route(): Route
}