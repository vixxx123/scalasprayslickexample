/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.github.vixxx123.scalasprayslickexample

import akka.actor.ActorSystem
import com.github.vixxx123.scalasprayslickexample.example.api.company.CompanyApiBuilder
import com.github.vixxx123.scalasprayslickexample.example.api.person.PersonApiBuilder
import com.github.vixxx123.scalasprayslickexample.example.auth.oauth2.{OauthAuthorization, OauthConfig}
import com.github.vixxx123.scalasprayslickexample.example.auth.oauth2.provider.MysqlAuthorizationProvider
import com.github.vixxx123.scalasprayslickexample.logger.ConsoleLogger
import com.github.vixxx123.scalasprayslickexample.rest.Rest


// Without authorization
object RestExampleApp extends App {
  new Rest(ActorSystem("on-spray-can"), List(new PersonApiBuilder,
    new CompanyApiBuilder), List(new ConsoleLogger)).start()
}


// With oauth 2 authorization
object RestWithOauthExampleApp extends App {
  val oauthConfig = new OauthConfig(new MysqlAuthorizationProvider())

  new Rest(ActorSystem("on-spray-can"), List(new PersonApiBuilder,
    new CompanyApiBuilder), List(new ConsoleLogger), new OauthAuthorization(oauthConfig)).start()
}
