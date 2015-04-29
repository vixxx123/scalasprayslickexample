/**
 * Created by Wiktor Tychulski.
 *
 * Created on 2015-04-29
 * Project: ScalaRest
 */
package com.vixxx123.scalasprayslickexample.rest.outh2

import java.util.Date

import akka.actor.{FSM, Actor}

class SessionDataWorker extends FSM[State, Option[Session]]{

  implicit def sessionToOption(session: Session): Option[Session] = Some(session)

  startWith(Init, None)

  when(Init) {
    case Event(initSession: Session, session) =>
      goto(Active) using initSession.copy(user = initSession.user.copy(lastLogin = Some(new Date)))
  }

  when(Active) {
    case Event(GetSession, session) =>
      sender() ! session
      stay using session
  }

}

private sealed trait State
private case object Init extends State
private case object Active extends State
