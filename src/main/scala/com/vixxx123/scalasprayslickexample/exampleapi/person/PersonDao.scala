/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.exampleapi.person

import com.vixxx123.scalasprayslickexample.database.{BaseT, BaseDbEntity}
import scala.slick.driver.MySQLDriver.simple._

class PersonDao extends BaseDbEntity[Person, PersonT]("person", TableQuery[PersonT])

class PersonT(tag: Tag) extends BaseT[Person](tag, "person") {
  def name: Column[String] = column[String]("name")
  def lastname: Column[String] = column[String]("lastname")

  override def * = (id.?, name, lastname) <> (
    (Person.apply _).tupled, Person.unapply)
}
