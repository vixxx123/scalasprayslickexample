package com.vixxx123.scalasprayslickexample.database

import com.vixxx123.scalasprayslickexample.entity.BaseEntity
import scala.slick.driver.MySQLDriver.simple._
import scala.slick.jdbc.{StaticQuery => Q}
import scala.slick.jdbc.meta.MTable

/**
 * Created by WiktorT on 24/04/2015.
 */
class BaseDbEntity[T <: BaseEntity, R <: BaseT[T]](val tableName: String, tableQuery: TableQuery[R]) extends DatabaseAccess{

  val createReturningId = tableQuery returning tableQuery.map{item => item.id}

  def create(entity: T): Int = {
    connectionPool withSession {
      implicit session =>
        val resId = createReturningId += entity
        resId
    }
  }

  def getAll = {
    connectionPool withSession {
      implicit session =>
        tableQuery.list
    }
  }

  def getById(id: Int) = {
    connectionPool withSession {
      implicit session =>
        tableQuery.filter(_.id === id).firstOption
    }
  }

  def update(entity: T): Int = {
    connectionPool withSession {
      implicit session =>
        tableQuery.filter(_.id === entity.id).update(entity)
    }
  }

  def deleteById(id: Int):Int = {
    connectionPool withSession {
      implicit session =>
        tableQuery.filter(_.id === id).delete
    }
  }

  def runQuery(query: String): Int = {
    connectionPool withSession {
      implicit session =>
        Q.updateNA(query).first
    }
  }

  def initTable(): Unit = {
    connectionPool withSession {
      implicit session =>
        if (MTable.getTables(tableName).list.isEmpty) {
          tableQuery.ddl.create
        }
    }
  }

}

abstract class BaseT[T](tag: Tag, tableName: String) extends Table[T](tag, tableName) {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
}
