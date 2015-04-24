package com.vixxx123.scalasprayslickexample.exampleapi

import com.vixxx123.scalasprayslickexample.entity.BaseEntity
import spray.json.DefaultJsonProtocol._

import scala.slick.driver.MySQLDriver.simple._

package object company {

  val ResourceName = "company"

  case class Company(id: Option[Int] = None, name: String, address: String) extends BaseEntity {
    def getId = id.getOrElse(throw new Exception("Getting id of not persisted entity"))
  }

  implicit val CompanyFormat = jsonFormat3(Company)

  implicit val CompanyDeleteFormat = jsonFormat1(DeleteResult)
}
