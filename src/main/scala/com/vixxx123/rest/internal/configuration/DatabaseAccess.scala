package com.vixxx123.rest.internal.configuration

import scala.slick.driver.MySQLDriver.simple._
import com.mchange.v2.c3p0.ComboPooledDataSource

trait DatabaseAccess {
  val connectionPool = DatabaseAccess.DatabasePool
}

object DatabaseAccess {
  val Url = "jdbc:mysql://localhost:3306/rest"
  val Driver = "com.mysql.jdbc.Driver"

  val DatabasePool = {
    val ds = new ComboPooledDataSource
    ds.setDriverClass(Driver)
    ds.setJdbcUrl(Url)
    ds.setUser("rest")
    ds.setPassword("CoMModore64")
    ds.setMinPoolSize(20)
    ds.setAcquireIncrement(5)
    ds.setMaxPoolSize(100)
    Database.forDataSource(ds)
  }
}
