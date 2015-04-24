package com.vixxx123.scalasprayslickexample.exampleapi.person

import com.vixxx123.scalasprayslickexample.database.{BaseT, BaseDbEntity}
import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by WiktorT on 24/04/2015.
 */
object PersonDb extends BaseDbEntity[Person, PersonT]("person", TableQuery[PersonT]) {

}

class PersonT(tag: Tag) extends BaseT[Person](tag, "person") {
  def name: Column[String] = column[String]("name")
  def lastname: Column[String] = column[String]("lastname")

  def * = (id.?, name, lastname) <> (
    (Person.apply _).tupled, Person.unapply)
}
