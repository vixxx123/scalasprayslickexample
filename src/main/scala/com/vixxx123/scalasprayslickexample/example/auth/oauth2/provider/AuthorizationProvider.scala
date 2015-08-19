package com.vixxx123.scalasprayslickexample.example.auth.oauth2.provider

import com.vixxx123.scalasprayslickexample.example.auth.oauth2.OauthUser

/**
 * Created by wiktort on 12/05/2015.
 *
 * Created on 12/05/2015
 */
trait AuthorizationProvider {
  def login(userToLogin: OauthUser): Option[OauthUser]
}
