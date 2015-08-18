/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.websocket

import java.util.concurrent.TimeUnit

import akka.pattern.ask
import akka.actor.{ActorRefFactory, Props, ActorRef}
import akka.util.Timeout
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.auth.Authorization
import com.vixxx123.scalasprayslickexample.rest.oauth2._
import com.vixxx123.scalasprayslickexample.rest.oauth2.session.{SessionService, GetSession, Session}
import spray.can.websocket
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.TextFrame
import spray.http.{StatusCodes, HttpResponse, HttpRequest}
import spray.routing.HttpServiceActor

import scala.concurrent.Await


object WebSocketWorker {
  def props(serverConnection: ActorRef, authorization: Authorization) = Props(classOf[WebSocketWorker], serverConnection, authorization)
}

class WebSocketWorker(val serverConnection: ActorRef, authorization: Authorization)
  extends HttpServiceActor with Logging with websocket.WebSocketServerWorker {

  var user: Option[OauthUser] = None

  override def receive = auth orElse handshaking orElse businessLogicNoUpgrade orElse closeLogic

  def auth: Receive = {


    case req: HttpRequest if oauthConfig.isDefined && !req.headers.exists(header => header.name.equals("Authorization")) =>
      L.debug("Unauthorized: header are missing")
      sender() ! HttpResponse(StatusCodes.Unauthorized)

    case req: HttpRequest if oauthConfig.isDefined && !authorized(req.headers.filter(header => header.name.equals("Authorization")).head.value) =>
      sender() ! HttpResponse(StatusCodes.Unauthorized)
  }

  private def authorized(token: String): Boolean = {
    val authKey = token.split(" ")
    implicit val timeout = Timeout(1, TimeUnit.SECONDS)
    implicit val ec = context.dispatcher

    user = Await.result((SessionService.getSessionManager ? new GetSession(authKey(1))).recover{case e: Exception => None}.mapTo[Option[Session]].map {
      case Some(res) => Some(res.user)
      case None => None
    }, timeout.duration)

    user.isDefined
  }

  override def businessLogic: Receive = {

    // ping-pong / no buisnes logic is needed for now on incoming messages
    case x @ TextFrame(data) =>
      sender() ! x

    // push message to client
    case Push(msg) => send(TextFrame(msg))

    case PushToUser(authUser, msg) =>
      user match {
        case Some(usr) =>
          if (authUser == usr.getId)
            send(TextFrame(msg))
        case None =>

      }

    case x: FrameCommandFailed =>
      log.error("frame command failed", x)
  }

  // only for testing purpose - should be configurable
  def businessLogicNoUpgrade: Receive = {
    implicit val refFactory: ActorRefFactory = context

    runRoute {
      getFromResourceDirectory("webapp")
    }
  }

  override val logTag: String = getClass.getName
}
