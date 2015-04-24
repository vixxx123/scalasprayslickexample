package com.vixxx123.scalasprayslickexample.exampleapi.company

import com.vixxx123.scalasprayslickexample.database.{BaseT, BaseDbEntity}
import scala.slick.driver.MySQLDriver.simple._

/**
 * Created by WiktorT on 24/04/2015.
 */
class CompanyDb extends BaseDbEntity[Company, CompanyT](ResourceName, TableQuery[CompanyT])

class CompanyT(tag: Tag) extends BaseT[Company](tag, ResourceName) {
  def name: Column[String] = column[String]("name")
  def address: Column[String] = column[String]("address")

  override def * = (id.?, name, address) <> (
    (Company.apply _).tupled, Company.unapply)
}
