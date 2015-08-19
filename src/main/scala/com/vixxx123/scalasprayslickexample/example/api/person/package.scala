/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.example.api

import com.vixxx123.scalasprayslickexample.entity.BaseEntity
import spray.json.DefaultJsonProtocol._



package object person {

  val ResourceName = "person"

  case class Person(id: Option[Int] = None, name: String, lastname: String) extends BaseEntity

  implicit val PersonFormat = jsonFormat3(Person)

  implicit val DeleteFormat = jsonFormat1(DeleteResult)


}
