package com.vixxx123.scalasprayslickexample

import akka.actor.ActorSystem
import com.vixxx123.scalasprayslickexample.logger.ConsoleLogger
import com.vixxx123.scalasprayslickexample.exampleapi.company.CompanyApi
import com.vixxx123.scalasprayslickexample.exampleapi.person.PersonApi
import com.vixxx123.scalasprayslickexample.rest.Rest

/**
 * Created by WiktorT on 24/04/2015.
 */
object RestExampleApp extends App{
  new Rest(ActorSystem("on-spray-can"), List(PersonApi, CompanyApi), List(new ConsoleLogger))
}
