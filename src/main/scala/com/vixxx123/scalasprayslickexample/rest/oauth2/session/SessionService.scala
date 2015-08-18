package com.vixxx123.scalasprayslickexample.rest.oauth2.session

import akka.actor.{ActorRef, ActorSystem}
import com.vixxx123.scalasprayslickexample.rest.oauth2.provider.AuthorizationProvider

/**
 * Created by wiktort on 11/05/2015.
 *
 * Created on 11/05/2015
 */
object SessionService {

  private var sessionManager: Option[ActorRef] = None

  def init(authProvider: AuthorizationProvider)(implicit actorSystem: ActorSystem): Unit = {
    sessionManager = Some(actorSystem.actorOf(SessionManager.props(authProvider), SessionManager.Name))
  }

  def getSessionManager: ActorRef = {
    sessionManager.getOrElse(throw new IllegalStateException("SessionService wasn't initialized"))
  }
}
