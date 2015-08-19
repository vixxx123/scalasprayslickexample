/**
 * Created by Wiktor Tychulski on 2015-08-19.
 *
 * Created on 2015-08-19
 */

package com.vixxx123.scalasprayslickexample.rest.auth

import akka.actor.ActorSystem
import com.vixxx123.scalasprayslickexample.rest.BaseResourceBuilder
import spray.routing.authentication._
import spray.routing.RequestContext

import scala.concurrent.{ExecutionContext, Future}

/**
 * Base class for Authorization
 *
 */
trait Authorization {

  def init()(implicit actorSystem: ActorSystem)

  def getAuthApiBuilder: Option[BaseResourceBuilder]

  def authFunction(implicit executionContext: ExecutionContext): (RequestContext) => Future[Authentication[RestApiUser]]

  def authenticator(implicit executionContext: ExecutionContext): ContextAuthenticator[RestApiUser] = new BaseAuth(authFunction)
}

/**
 * No authorization - all request will pass
 */
object NoAuthorisation extends Authorization {

  override def init()(implicit actorSystem: ActorSystem) {}

  override def getAuthApiBuilder: Option[BaseResourceBuilder] = None

  override def authFunction(implicit executionContext: ExecutionContext) = {
    context =>
      Future.successful(Right(NoAuthUser))
  }

}

class BaseAuth(auth: (RequestContext) => Future[Authentication[RestApiUser]])(implicit executionContext: ExecutionContext)
  extends ContextAuthenticator[RestApiUser] {

  override def apply(context: RequestContext): Future[Authentication[RestApiUser]] = {
    auth(context)
  }

}
