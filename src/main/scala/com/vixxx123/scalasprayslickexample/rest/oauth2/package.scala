/**
 * Created by Wiktor
 *
 * Created on 2015-05-11.
 */
package com.vixxx123.scalasprayslickexample.rest

import spray.json.DefaultJsonProtocol._


package object oauth2 {
  val ResourceName = "oauth"

  implicit val AuthUserFormat = jsonFormat3(AuthUser)

  case class TokenResponse(access_token: String, expires_in: Long, token_type: String = "Bearer")
  implicit val TokenJsonFormat = jsonFormat3(TokenResponse)

}
