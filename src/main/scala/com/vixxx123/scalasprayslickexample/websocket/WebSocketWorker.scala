/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.websocket

import akka.actor.{ActorRefFactory, Props, ActorRef}
import spray.can.websocket
import spray.can.websocket.FrameCommandFailed
import spray.can.websocket.frame.TextFrame
import spray.routing.HttpServiceActor


object WebSocketWorker {
  def props(serverConnection: ActorRef) = Props(classOf[WebSocketWorker], serverConnection)
}

class WebSocketWorker(val serverConnection: ActorRef) extends HttpServiceActor with websocket.WebSocketServerWorker {

  override def receive = handshaking orElse businessLogicNoUpgrade orElse closeLogic

  override def businessLogic: Receive = {

    // ping-pong / no buisnes logic is needed for now on incoming messages
    case x @ TextFrame(data) =>
      sender() ! x

    // push message to client
    case Push(msg) => send(TextFrame(msg))

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
}
