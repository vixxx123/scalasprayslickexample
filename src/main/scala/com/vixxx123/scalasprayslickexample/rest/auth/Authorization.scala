/**
 * Created by Wiktor
 *
 * Created on 2015-05-11.
 */
package com.vixxx123.scalasprayslickexample.rest.auth

import spray.routing.authentication._
import spray.routing.RequestContext

import scala.concurrent.{ExecutionContext, Future}

trait Authorization {

  def authFunction(implicit executionContext: ExecutionContext): (RequestContext) => Future[Authentication[RestApiUser]]

  def authenticator(implicit executionContext: ExecutionContext): ContextAuthenticator[RestApiUser] = new BaseAuth(authFunction)
}


object NoAuthorisation extends Authorization {

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
