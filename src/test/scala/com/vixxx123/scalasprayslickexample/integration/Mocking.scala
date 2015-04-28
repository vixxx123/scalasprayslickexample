/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.integration

import akka.actor.ActorContext
import com.vixxx123.scalasprayslickexample.exampleapi.company.{Company, CompanyApiBuilder, CompanyApi, CompanyDao}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.mock.MockitoSugar


trait Mocking extends MockitoSugar {
  val companyDb = mock[CompanyDao]
  val companyApi = mock[CompanyApiBuilder]

  when(companyApi.create(any(classOf[ActorContext]))).thenAnswer(new Answer[CompanyApi] {
    override def answer(invocationOnMock: InvocationOnMock): CompanyApi = {
      val args = invocationOnMock.getArguments
      new CompanyApi(args(0).asInstanceOf[ActorContext], companyDb)
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
}
