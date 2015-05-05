/**
 * Created by WiktorT on 28/04/2015.
 *
 * Created on 28/04/2015
 */
package com.vixxx123.scalasprayslickexample.rest.outh2

import java.util.Date

case class AuthUser(id: Option[Int], username: String, password: String, lastLogin: Option[Date])

