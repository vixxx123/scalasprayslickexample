package com.vixxx123.rest

import spray.json.DefaultJsonProtocol._

import scala.slick.driver.MySQLDriver.simple._

package object company {

  val TableName = "company"

  case class Company(id: Option[Int] = None, name: String, address: String)

  class CompanyT(tag: Tag) extends Table[Company](tag, TableName) {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name")
    def address: Column[String] = column[String]("address")

    def * = (id.?, name, address) <> (
      (Company.apply _).tupled, Company.unapply)
  }

  implicit val CompanyFormat = jsonFormat3(Company)

  implicit val CompanyDeleteFormat = jsonFormat1(DeleteResult)

  val Companies: TableQuery[CompanyT] = TableQuery[CompanyT]

  val CompaniesIdReturning = Companies returning Companies.map{_.id}
}
