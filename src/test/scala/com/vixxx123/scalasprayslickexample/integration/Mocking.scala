package com.vixxx123.scalasprayslickexample.integration

import com.vixxx123.scalasprayslickexample.exampleapi.company.CompanyDao
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import spray.http.HttpRequest
import spray.routing.RequestContext

/**
 * Created by Wiktor Tychulski on 2015-04-24.
 */
trait Mocking extends MockitoSugar {
  val companyDb = mock[CompanyDao]
  val ctx = mock[RequestContext]
  val httpReq = mock[HttpRequest]

  doNothing().when(ctx).complete(any(AnyRef.getClass))(any())
  when(ctx.request).thenReturn(httpReq)
  when(httpReq.uri).thenReturn("")
  when(companyDb.create(any())).thenReturn(1)
}
