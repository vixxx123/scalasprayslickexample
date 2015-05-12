/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample

import akka.actor.ActorSystem
import com.vixxx123.scalasprayslickexample.logger.ConsoleLogger
import com.vixxx123.scalasprayslickexample.exampleapi.company.CompanyApiBuilder
import com.vixxx123.scalasprayslickexample.exampleapi.person.PersonApiBuilder
import com.vixxx123.scalasprayslickexample.rest.Rest
import com.vixxx123.scalasprayslickexample.rest.oauth2.{OauthConfig, AuthUserDao, MysqlAuthorizationProvider, OauthApiApiBuilder}

object RestExampleApp extends App{
  val oauthConfig = new OauthConfig(new OauthApiApiBuilder, new MysqlAuthorizationProvider(new AuthUserDao))
  new Rest(ActorSystem("on-spray-can"), List(new PersonApiBuilder,
    new CompanyApiBuilder), List(new ConsoleLogger)).withOauth(oauthConfig).start()
}
