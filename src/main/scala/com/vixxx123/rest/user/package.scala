package com.vixxx123.rest

import spray.json.DefaultJsonProtocol._

import scala.slick.driver.MySQLDriver.simple._

package object user {

  case class User(id: Option[Int] = None, name: String, login: String)

  class UserT(tag: Tag) extends Table[User](tag, "user") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name: Column[String] = column[String]("name")
    def login: Column[String] = column[String]("login")

    def * = (id.?, name, login) <> (
      (User.apply _).tupled, User.unapply)
  }

  implicit val PersonFormat = jsonFormat3(User)

  val Users: TableQuery[UserT] = TableQuery[UserT]

  val UsersIdReturning = Users returning Users.map{_.id}
}
