/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.integration

import akka.actor.ActorContext
import com.vixxx123.scalasprayslickexample.exampleapi.company.{Company, CompanyApiBuilder, CompanyApi, CompanyDao}
import com.vixxx123.scalasprayslickexample.rest.auth.{NoAuthorisation, Authorization}
import com.vixxx123.scalasprayslickexample.rest.oauth2._
import com.vixxx123.scalasprayslickexample.rest.oauth2.provider.MysqlAuthorizationProvider
import com.vixxx123.scalasprayslickexample.rest.oauth2.session.SessionService
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.mock.MockitoSugar


trait Mocking extends MockitoSugar {
  val companyDb = mock[CompanyDao]
  val companyApi = mock[CompanyApiBuilder]
  val oauthApi = mock[OauthApiBuilder]
  val oauthDao = mock[OauthUserDao]
  val oauthProvider = mock[MysqlAuthorizationProvider]


  when(companyApi.create(any(classOf[ActorContext]), any(classOf[Authorization]))).thenAnswer(new Answer[CompanyApi] {
    override def answer(invocationOnMock: InvocationOnMock): CompanyApi = {
      val args = invocationOnMock.getArguments
      new CompanyApi(args(0).asInstanceOf[ActorContext], companyDb, NoAuthorisation)
    }
  })

  when(oauthApi.create(any(classOf[ActorContext]))).thenAnswer(new Answer[OauthApi] {
    override def answer(invocationOnMock: InvocationOnMock): OauthApi = {
      val args = invocationOnMock.getArguments
      new OauthApi(args(0).asInstanceOf[ActorContext], SessionService.getSessionManager, oauthDao)
    }
  })

  when(companyDb.create(any())).thenReturn(1)
  when(companyDb.getById(any())).thenAnswer(new Answer[Option[Company]] {
    override def answer(invocationOnMock: InvocationOnMock): Option[Company] = {
      val args = invocationOnMock.getArguments
      if (args(0).asInstanceOf[Int] == 1){
        Option(new Company(Some(1), "test name", "test address"))
      } else {
        None
      }
    }
  })

  when(companyDb.getAll).thenReturn(List(new Company(Some(1), "test name", "test address"),
    new Company(Some(2), "test name", "test address")))
  when(companyDb.deleteById(any())).thenAnswer(new Answer[Int] {
    override def answer(invocationOnMock: InvocationOnMock): Int = {
      val args = invocationOnMock.getArguments
      if (args(0).asInstanceOf[Int] == 1){
        1
      } else {
        0
      }
    }
  })

  when(companyDb.update(any())).thenAnswer(new Answer[Int] {
    override def answer(invocationOnMock: InvocationOnMock): Int = {
      val args = invocationOnMock.getArguments
      if (args(0).asInstanceOf[Company].getId == 1){
        1
      } else {
        0
      }
    }
  })


  when(companyDb.patch(any(), any())).thenAnswer(new Answer[List[Int]] {
    override def answer(invocationOnMock: InvocationOnMock): List[Int] = {
      val args = invocationOnMock.getArguments
      if (args(1).asInstanceOf[Int] == 1){
        List(1)
      } else {
        List(0)
      }
    }
  })

  when(oauthProvider.login(any())).thenReturn(Some(new OauthUser(Some(1), "me", "passhash")))
}
