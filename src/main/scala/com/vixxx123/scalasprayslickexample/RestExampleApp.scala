package com.vixxx123.scalasprayslickexample

import akka.actor.ActorSystem
import com.vixxx123.scalasprayslickexample.logger.ConsoleLogger
import com.vixxx123.scalasprayslickexample.exampleapi.company.CompanyApiBuilder
import com.vixxx123.scalasprayslickexample.exampleapi.person.PersonApiBuilder
import com.vixxx123.scalasprayslickexample.rest.Rest

/**
 * Created by WiktorT on 24/04/2015.
 */
object RestExampleApp extends App{
  new Rest(ActorSystem("on-spray-can"), List(new PersonApiBuilder, new CompanyApiBuilder), List(new ConsoleLogger)).start()
}
