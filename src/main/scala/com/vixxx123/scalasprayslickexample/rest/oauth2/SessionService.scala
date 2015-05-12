package com.vixxx123.scalasprayslickexample.rest.oauth2

import akka.actor.{ActorRef, ActorSystem}

/**
 * Created by wiktort on 11/05/2015.
 *
 * Created on 11/05/2015
 */
object SessionService {

  var sessionManager: Option[ActorRef] = None

  def init(implicit actorSystem: ActorSystem): Unit = {
    sessionManager = Some(actorSystem.actorOf(SessionManager.props(), SessionManager.Name))
  }

  def getSessionManager: ActorRef = {
    sessionManager.getOrElse(throw new IllegalStateException("SessionService wasn't initialized"))
  }
}
