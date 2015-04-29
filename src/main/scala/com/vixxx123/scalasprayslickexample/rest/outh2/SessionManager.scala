/**
 * Created by Wiktor Tychulski.
 *
 * Created on 2015-04-29
 * Project: ${PROJECT_NAME}
 */
package com.vixxx123.scalasprayslickexample.rest.outh2

import akka.actor.{PoisonPill, ActorRef, Props, Actor}


class SessionManager extends Actor {

  override def receive: Receive = {
    case Session(token, user) =>
      val sessionWorker = context.actorOf(Props(classOf[SessionDataWorker]), s"session-$token")
      sessionWorker ! Session

    case GetSession(token) =>
      getSessionActor(token) match {
        case Some(worker) =>
          worker forward GetSession
      }

    case DestroySession(token) =>
      getSessionActor(token) match {
        case Some(worker) =>
          worker ! PoisonPill
      }
  }

  def getSessionActor(token: String): Option[ActorRef] = {
    context.child(s"session-$token")
  }
}

case class DestroySession(token: String)
case class GetSession(token: String)
private[outh2] case object GetSession
