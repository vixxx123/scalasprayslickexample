/**
 * Created by Wiktor Tychulski.
 *
 * Created on 2015-04-29
 * Project: ${PROJECT_NAME}
 */
package com.vixxx123.scalasprayslickexample.rest.oauth2

import akka.actor.Status.Failure
import akka.actor.{PoisonPill, ActorRef, Props, Actor}
import com.vixxx123.scalasprayslickexample.util.TokenUtil

import scala.util.Random


class SessionManager extends Actor {

  override def receive: Receive = {

    case Create(user) =>
      //token:
      val halfHour = 1000 * 60 * 30
      val random = new Random()

      val token = Token(TokenUtil.generateToken, System.currentTimeMillis() + SessionManager.LifeTimeInMilli)
      val sessionWorker = context.actorOf(Props(classOf[SessionDataWorker]), s"session-${token.accessToken}")
      sessionWorker forward Session(token, user)

    case GetSession(token) =>
      getSessionActor(token) match {
        case Some(worker) =>
          worker forward GetSession
        case None =>
          sender() ! Failure(SessionNotFound)
      }

    case DestroySession(token) =>
      getSessionActor(token) match {
        case Some(worker) =>
          worker ! KillSession
        case None =>
          sender() ! Failure(SessionNotFound)
      }
  }

  private def getSessionActor(token: String): Option[ActorRef] = {
    context.child(s"session-$token")
  }
}

object SessionManager {
  val LifeTimeInSec = 60 * 30
  val LifeTimeInMilli = 1000 * LifeTimeInSec

  val Name = "SessionManager"
  def props() = Props(classOf[SessionManager])
}

case class Create(user: AuthUser)
case class DestroySession(token: String)
case class GetSession(token: String)
case object KillSession
case object SessionNotFound extends Exception
private[oauth2] case object GetSession
