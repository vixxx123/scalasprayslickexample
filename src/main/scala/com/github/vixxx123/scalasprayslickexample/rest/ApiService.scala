/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.github.vixxx123.scalasprayslickexample.rest

import akka.actor._
import com.github.vixxx123.scalasprayslickexample.logger.Logging
import com.github.vixxx123.scalasprayslickexample.rest.auth.Authorization
import spray.routing._
import spray.json.DefaultJsonProtocol._
import spray.routing.authentication._

/**
 * Main Api service class
 */
class ApiService(availableApis: List[BaseResourceBuilder], authorization: Authorization)
  extends Actor with HttpServiceBase with Logging {

  val apis = availableApis.map{_.create(context, authorization)}
  apis.foreach(_.init())

  val routing: Route = apis.foldLeft[Route](null)((a,b) => if (a == null) b.apiRoute() else {a ~ b.apiRoute()})

  override val logTag: String = getClass.getName

  override def receive = runRoute(handleExceptions(new RestExceptionHandler().exceptionHandler){routing})

}

object ApiService {
  val ActorName = "api-root"

  def props(availableApis: List[BaseResourceBuilder],  authorization: Authorization) = {
    Props(classOf[ApiService], availableApis, authorization: Authorization)
  }
}
