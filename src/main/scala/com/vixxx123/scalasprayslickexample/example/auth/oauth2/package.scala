/**
 * Created by Wiktor
 *
 * Created on 2015-05-11.
 */
package com.vixxx123.scalasprayslickexample.example.auth

import spray.json.DefaultJsonProtocol._


package object oauth2 {
  val ResourceName = "oauth"

  implicit val OauthUserFormat = jsonFormat3(OauthUser)

  case class TokenResponse(access_token: String, expires_in: Long, token_type: String = "Bearer")
  implicit val TokenJsonFormat = jsonFormat3(TokenResponse)

}
