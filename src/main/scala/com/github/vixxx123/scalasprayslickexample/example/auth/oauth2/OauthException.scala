package com.github.vixxx123.scalasprayslickexample.example.auth.oauth2

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
  extends OauthException(OauthException.AuthHeaderIsMissing, cause) {

  def this() = this(null)
}

class TokenIsMissingException(cause: Exception)
  extends OauthException(OauthException.TokenIsMissing, cause) {

  def this() = this(null)
}

class UnknownTokenTypeException(cause: Exception)
  extends OauthException(OauthException.UnknownTokenType, cause) {

  def this() = this(null)
}


object OauthException {
  val AuthHeaderIsMissing = "Header with token is missing"
  val TokenIsMissing = "Token is missing"
  val IncorrectAuthenticationHeader = "Incorrect structure of Authentication header"
  val UnknownTokenType = "Unknown token type"
}