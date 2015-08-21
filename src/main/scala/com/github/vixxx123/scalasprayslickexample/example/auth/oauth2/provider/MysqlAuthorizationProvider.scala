/**
 * Created by wiktort on 12/05/2015.
 *
 * Created on 12/05/2015
 */
package com.github.vixxx123.scalasprayslickexample.example.auth.oauth2.provider

import com.github.vixxx123.scalasprayslickexample.database.DatabaseAccess
import com.github.vixxx123.scalasprayslickexample.example.auth.oauth2.{OauthUser, OauthUserT}
import com.github.vixxx123.scalasprayslickexample.util.CryptoUtil

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.lifted.TableQuery

class MysqlAuthorizationProvider() extends AuthorizationProvider with DatabaseAccess{

  override def login(userToLogin: OauthUser): Option[OauthUser] = {
    val password = CryptoUtil.sha1(userToLogin.password.toCharArray.map{_.toByte})

    connectionPool withSession {
      implicit session =>
        TableQuery[OauthUserT].filter(item => item.username === userToLogin.username &&
          item.password === password).firstOption
    }
  }
}
