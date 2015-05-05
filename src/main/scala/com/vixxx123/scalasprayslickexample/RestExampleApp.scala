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
import com.vixxx123.scalasprayslickexample.rest.outh2.OauthApiApiBuilder

object RestExampleApp extends App{
  new Rest(ActorSystem("on-spray-can"), List(new PersonApiBuilder, new CompanyApiBuilder, new OauthApiApiBuilder), List(new ConsoleLogger)).start()
}
