/**
 * Created by WiktorT on 28/04/2015.
 *
 * Created on 28/04/2015
 */
package com.vixxx123.scalasprayslickexample.rest.oauth2

import com.vixxx123.scalasprayslickexample.entity.BaseEntity
import com.vixxx123.scalasprayslickexample.rest.auth.AuthenticatedUser

case class OauthUser(id: Option[Int] = None, username: String, password: String) extends BaseEntity with AuthenticatedUser