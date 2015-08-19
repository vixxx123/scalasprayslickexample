/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.rest

import akka.actor._
import com.vixxx123.scalasprayslickexample.entity.JsonNotation
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.auth.{NoAuthorisation, Authorization, RestApiUser}
import spray.routing._
import spray.json.DefaultJsonProtocol._
import spray.routing.authentication._

/**
 * Main Api service class
 */
class ApiService(availableApis: List[Api], authorization: Authorization) extends Actor with HttpServiceBase with Logging {

  val apis = availableApis.map{_.create(context, authorization)}
  apis.foreach(_.init())

  val routing: Route = apis.foldLeft[Route](null)((a,b) => if (a == null) b.apiRoute() else {a ~ b.apiRoute()})

  override val logTag: String = getClass.getName

  override def receive = runRoute(handleExceptions(new RestExceptionHandler().exceptionHandler){routing})

}

object ApiService {
  val ActorName = "api-root"

  def props(availableApis: List[Api],  authorization: Authorization) = {
    Props(classOf[ApiService], availableApis, authorization: Authorization)
  }
}


trait Api {
  def create(actorContext: ActorContext, auth: Authorization): BaseResourceApi
}

trait BaseResourceApi extends HttpServiceBase{

  val actorContext: ActorContext
  implicit val ec = actorContext.dispatcher

  implicit val JsonNotationFormat = jsonFormat3(JsonNotation)

  def authorisedResource: Boolean

  def init(): Unit = {}

  def route(implicit user: RestApiUser): Route

  def apiRoute() = auth{ user => route(user) }

  def authorization: Authorization

  def auth: Directive1[RestApiUser] = {
    val authenticator = authorisedResource match {
      case true => authorization.authenticator
      case false => NoAuthorisation.authenticator
    }

    authenticate(authenticator)
  }
}