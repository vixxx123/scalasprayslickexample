/**
 * Created by wiktort on 12/05/2015.
 *
 * Created on 12/05/2015
 */
package com.vixxx123.scalasprayslickexample.rest.oauth2

import com.vixxx123.scalasprayslickexample.database.DatabaseAccess
import scala.slick.driver.MySQLDriver.simple._
import com.vixxx123.scalasprayslickexample.util.TokenUtil

import scala.slick.lifted.TableQuery

class MysqlAuthorizationProvider(authUserDao: AuthUserDao) extends AuthorizationProvider with DatabaseAccess{

  override def login(userToLogin: AuthUser): Option[AuthUser] = {
    val password = TokenUtil.sha1(userToLogin.password.toCharArray.map{_.toByte})

    connectionPool withSession {
      implicit session =>
        TableQuery[AuthUserT].filter(item => item.username === userToLogin.username &&
          item.password === password).firstOption
    }
  }
}
