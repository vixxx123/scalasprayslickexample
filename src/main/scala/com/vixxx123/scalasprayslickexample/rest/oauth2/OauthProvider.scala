/**
 * Created by Wiktor Tychulski.
 *
 * Created on 2015-04-29
 * Project: ${PROJECT_NAME}
 */
package com.vixxx123.scalasprayslickexample.rest.oauth2

trait OauthProvider {

  def login(username: String, password: String): Token

}

trait OauthAuthentication {

  def authenticate(token: String): Boolean

}