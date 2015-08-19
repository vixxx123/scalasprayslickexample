/**
 * Created by WiktorT on 28/04/2015.
 *
 * Created on 28/04/2015
 */
package com.vixxx123.scalasprayslickexample.example.auth.oauth2.session

import com.vixxx123.scalasprayslickexample.example.auth.oauth2.{OauthUser, Token}

case class Session(token: Token, user: OauthUser)

