/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.rest.oauth2

import java.util.concurrent.TimeUnit

import akka.pattern.ask
import akka.actor.{ActorRef, ActorContext}
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.{Api, BaseResourceApi}
import spray.http.FormData
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import scala.util.{Failure, Success}

/**
 * Company API main class
 *
 * trait HttpService - for spray routing
 * trait BaseResourceApi - for initialization
 * trait DatabaseAccess - for db access
 *
 */
class OauthApi(val actorContext: ActorContext, sessionManager: ActorRef) extends BaseResourceApi with Logging {

  private implicit val ec = actorContext.dispatcher
  private implicit val timeout = Timeout(1, TimeUnit.SECONDS)

  override val logTag: String = getClass.getName

  override def init() = {
    super.init()
  }

  override def route() =
    pathPrefix(ResourceName) {
      pathEnd {
        post {
          entity(as[FormData]) {
            user =>
              ctx => {
                val localCtx = ctx

                val username = user.fields.filter(item => item._1 == "username").head._2
                val password = user.fields.filter(item => item._1 == "password").head._2
                val login = sessionManager ? Create(AuthUser(None, username = username, password = password))
                login.mapTo[Session].onComplete{
                  case Success(result) =>
                    localCtx.complete(TokenResponse(result.token.accessToken, SessionManager.LifeTimeInSec))
                  case Failure(e) =>
                    localCtx.complete(e)
                }
              }
          }
        }
      }
    }
}

class OauthApiApiBuilder extends Api{
  override def create(actorContext: ActorContext): BaseResourceApi = {
    new OauthApi(actorContext, SessionService.getSessionManager)
  }
}
