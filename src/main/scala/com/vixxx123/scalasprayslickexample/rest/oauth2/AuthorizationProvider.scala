package com.vixxx123.scalasprayslickexample.rest.oauth2

/**
 * Created by wiktort on 12/05/2015.
 *
 * Created on 12/05/2015
 */
trait AuthorizationProvider {
  def login(userToLogin: AuthUser): Option[AuthUser]
}
