/**
 * Created by Wiktor
 *
 * Created on 2015-05-11.
 */
package com.vixxx123.scalasprayslickexample.rest.oauth2

import java.util.concurrent.TimeUnit

import akka.actor.ActorContext
import akka.pattern.ask
import akka.util.Timeout
import spray.routing.{Directive1, HttpServiceBase}

import scala.concurrent.{ExecutionContext, Promise}
import scala.util.{Failure, Success}

trait Authorization extends HttpServiceBase {

  val actorContext: ActorContext
  implicit val ec: ExecutionContext = actorContext.dispatcher

  private def authenticator = TokenAuthenticator[AuthUser](
    headerName = "access_token",
    queryStringParameterName = "access_token"
  ) { key =>
    implicit val timeout = Timeout(1, TimeUnit.SECONDS)
    (SessionService.getSessionManager ? new GetSession(key)).recover{case e: Exception => None}.mapTo[Option[Session]].map {
      case Some(res) => Some(res.user)
      case None => None
    }
  }

  def auth: Directive1[AuthUser] = authenticate(authenticator)
}
