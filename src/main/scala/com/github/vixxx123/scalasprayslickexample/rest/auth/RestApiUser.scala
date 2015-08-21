package com.github.vixxx123.scalasprayslickexample.rest.auth

/**
 * Created by wiktort on 18/08/2015.
 *
 * Created on 18/08/2015
 */
trait RestApiUser{
  def id: Option[Int]
}

object NoAuthUser extends RestApiUser {
  override def id: Option[Int] = Some(1)
}

trait AuthenticatedUser extends RestApiUser {
  def username(): String
  def password(): String
}