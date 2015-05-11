/**
 * Created by Wiktor Tychulski.
 *
 * Created on 2015-04-29
 * Project: ScalaRest
 */
package com.vixxx123.scalasprayslickexample.rest.outh2

import java.util.concurrent.TimeUnit

import akka.actor.{FSM, Actor}
import akka.util.Timeout

class SessionDataWorker extends FSM[State, Option[Session]]{

  val TokenTimeout = "TOKEN_TIMEOUT"

  implicit def sessionToOption(session: Session): Option[Session] = Some(session)

  startWith(Init, None)

  when(Init) {
    case Event(initSession: Session, session) =>
      setTimer(TokenTimeout, KillSession,
        Timeout(initSession.token.expires - System.currentTimeMillis(), TimeUnit.MILLISECONDS).duration, repeat = false)
      sender() ! initSession
      goto(Active) using initSession
  }

  when(Active) {
    case Event(GetSession, session) =>
      sender() ! session.get
      stay using session

    case Event(KillSession, session) =>
      cancelTimer(TokenTimeout)
      stop()
  }

}

sealed trait State
case object Init extends State
case object Active extends State
