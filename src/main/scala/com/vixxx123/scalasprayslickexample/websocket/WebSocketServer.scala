package com.vixxx123.scalasprayslickexample.websocket

import akka.actor.{Props, Actor}
import com.vixxx123.scalasprayslickexample.logger.Logging
import spray.can.Http

class WebSocketServer extends Actor with Logging {

  def receive = {
    // when a new connection comes in we register a WebSocketConnection actor as the per connection handler
    case Http.Connected(remoteAddress, localAddress) =>
      val serverConnection = sender()
      // should be match with a session/user - this way it will be possible to push message only to specific user
      val conn = context.actorOf(WebSocketWorker.props(serverConnection))
      serverConnection ! Http.Register(conn)

    case push: Push =>
      // Push message to all open connections
      context.children.foreach{_ ! push}
  }

  override val logTag: String = getClass.getName
}

object WebSocketServer {
  def props() = Props(classOf[WebSocketServer])
}

final case class Push(msg: String)