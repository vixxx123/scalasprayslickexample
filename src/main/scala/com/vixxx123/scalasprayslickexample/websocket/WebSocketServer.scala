/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.websocket

import akka.actor.{Props, Actor}
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.oauth2.OauthConfig
import spray.can.Http


class WebSocketServer(oauthConfig: Option[OauthConfig]) extends Actor with Logging {

  def receive = {

    // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
    case req@Http.Connected(remoteAddress, localAddress) =>

      val serverConnection = sender()
      // should be match with a session/user - this way it will be possible to push message only to specific user
      val conn = context.actorOf(WebSocketWorker.props(serverConnection, oauthConfig))
      serverConnection ! Http.Register(conn)

    case push: Push =>
      // Push message to all open connections
      context.children.foreach {
        _ ! push
      }

    case push: PushToUser =>
      println(push)

  }

  override val logTag: String = getClass.getName
}

object WebSocketServer {
  val Name = "websocket"

  def props(oauthConfig: Option[OauthConfig]) = Props(classOf[WebSocketServer], oauthConfig)
}

final case class Push(msg: String)

final case class PushToUser(usrId: Int, msg: String)