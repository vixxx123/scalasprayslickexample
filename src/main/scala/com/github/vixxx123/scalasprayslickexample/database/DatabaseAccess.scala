/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.github.vixxx123.scalasprayslickexample.database

import com.mchange.v2.c3p0.ComboPooledDataSource
import com.typesafe.config.ConfigFactory

import scala.slick.driver.MySQLDriver
import scala.slick.driver.MySQLDriver.simple._

/**
 * Database connection pool trait / mix it wherever db access is needed
 */
trait DatabaseAccess {
  lazy val connectionPool: MySQLDriver.backend.DatabaseDef = DatabaseAccess.DatabasePool
}

private object DatabaseAccess {
  val Driver = "com.mysql.jdbc.Driver"

  val DatabasePool = {
    val conf = ConfigFactory.load()
    val ds = new ComboPooledDataSource
    ds.setDriverClass(Driver)
    ds.setJdbcUrl(conf.getString("db.dbUri"))
    ds.setUser(conf.getString("db.user"))
    ds.setPassword(conf.getString("db.password"))
    ds.setMinPoolSize(20)
    ds.setAcquireIncrement(5)
    ds.setMaxPoolSize(100)
    Database.forDataSource(ds)
  }
}
