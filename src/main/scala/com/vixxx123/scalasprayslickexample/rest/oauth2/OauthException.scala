package com.vixxx123.scalasprayslickexample.rest.oauth2

/**
 * Created by Wiktor 
 *
 * Created on 2015-08-18.
 */

class OauthException(msg: String, cause: Exception) extends Exception(msg, cause) {
  def this(msg: String) = this(msg, null)
}

class IncorrectAuthenticationHeaderException(cause: Exception)
  extends OauthException(OauthException.IncorrectAuthenticationHeader, cause) {

  def this() = this(null)
}

class AuthHeaderIsMissingException(cause: Exception)
  extends OauthException(OauthException.IncorrectAuthenticationHeader, cause) {

  def this() = this(null)
}

object OauthException {
  val AuthHeaderIsMissing = "Header with token is missing"
  val IncorrectAuthenticationHeader = "Incorrect structure of Authentication header"
}