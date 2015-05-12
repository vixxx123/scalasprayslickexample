/**
 * Created by WiktorT on 28/04/2015.
 *
 * Created on 28/04/2015
 */
package com.vixxx123.scalasprayslickexample.rest.oauth2

import java.util.Date

import com.vixxx123.scalasprayslickexample.entity.BaseEntity

case class AuthUser(id: Option[Int] = None, username: String, password: String) extends BaseEntity

