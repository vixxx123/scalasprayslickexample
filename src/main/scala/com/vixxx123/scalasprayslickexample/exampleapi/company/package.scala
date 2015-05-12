/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.exampleapi

import com.vixxx123.scalasprayslickexample.entity.BaseEntity
import spray.json.DefaultJsonProtocol._

import scala.slick.driver.MySQLDriver.simple._

package object company {

  val ResourceName = "company"

  case class Company(id: Option[Int] = None, name: String, address: String) extends BaseEntity

  implicit val CompanyFormat = jsonFormat3(Company)
}
