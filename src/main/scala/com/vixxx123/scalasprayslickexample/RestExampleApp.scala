/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample

import akka.actor.ActorSystem
import com.vixxx123.scalasprayslickexample.logger.ConsoleLogger
import com.vixxx123.scalasprayslickexample.example.api.company.CompanyApiBuilder
import com.vixxx123.scalasprayslickexample.example.api.person.PersonApiBuilder
import com.vixxx123.scalasprayslickexample.rest.Rest
import com.vixxx123.scalasprayslickexample.example.auth.oauth2.provider.MysqlAuthorizationProvider
import com.vixxx123.scalasprayslickexample.example.auth.oauth2.{OauthAuthorization, OauthConfig, OauthUserDao, OauthApiBuilder}


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
