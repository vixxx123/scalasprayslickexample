package com.vixxx123.scalasprayslickexample.rest.oauth2

import java.util.concurrent.TimeUnit

import akka.pattern.ask
import akka.util.Timeout
import com.vixxx123.scalasprayslickexample.rest.auth.{AuthenticatedUser, RestApiUser, Authorization}
import com.vixxx123.scalasprayslickexample.rest.oauth2.session.{SessionService, GetSession, Session}
import spray.http.HttpRequest
import spray.routing.RequestContext
import spray.routing.authentication.Authentication

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by wiktort on 18/08/2015.
 *
 * Created on 18/08/2015
 */
class OauthAuthorization extends Authorization {

  override def authFunction(implicit executionContext: ExecutionContext): (RequestContext) => Future[Authentication[RestApiUser]] = {

    TokenAuthenticator[AuthenticatedUser](
      headerName = "Authorization",
      queryStringParameterName = "access_token"
    ) { key =>
      val authKey = key.split(" ")
      implicit val timeout = Timeout(1, TimeUnit.SECONDS)
      (SessionService.getSessionManager ? new GetSession(authKey(1))).recover{case e: Exception => None}.mapTo[Option[Session]].map {
        case Some(res) => Some(res.user)
        case None => None
      }
    }
  }
}

object OauthRequestParser {

  val AuthorizationHeader = "Authorization"

  def tokenExists(request: HttpRequest) = request.headers.exists(_.name == AuthorizationHeader)

  def getToken(request: HttpRequest) = {
    val tokenHeader = request.headers.find(_.name == AuthorizationHeader).get.value
    val authData = tokenHeader.split(" ")
    if (authData.size != 2) {

    }
  }
}

