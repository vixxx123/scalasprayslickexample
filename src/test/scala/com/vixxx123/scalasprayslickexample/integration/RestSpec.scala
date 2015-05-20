/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.integration

import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.util.Timeout
import com.vixxx123.scalasprayslickexample.database.DatabaseAccess
import com.vixxx123.scalasprayslickexample.exampleapi.company._
import com.vixxx123.scalasprayslickexample.logger.ConsoleLogger
import com.vixxx123.scalasprayslickexample.rest.Rest
import com.vixxx123.scalasprayslickexample.rest.oauth2.{AuthUserDao, OauthConfig}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, FlatSpec}
import spray.http.HttpHeaders.RawHeader

import spray.http._
import scala.concurrent.{Await, Future}
import spray.client.pipelining._
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

@RunWith(classOf[JUnitRunner])
class RestSpec extends FlatSpec with Mocking with BeforeAndAfterAll with DatabaseAccess {

  implicit val system = ActorSystem("test")
  implicit val ec = system.dispatcher
  val rest = new Rest(system, List(companyApi), List(new ConsoleLogger), Some(OauthConfig(oauthApi, oauthProvider)))

  implicit val timeout = Timeout(2000, TimeUnit.SECONDS)


  "Rest company api" should "be able to create new company" in {
      val pipeline: HttpRequest => Future[Company] = (
        // we want to get json
        ((_: HttpRequest).mapEntity(_.flatMap(f => HttpEntity(
          f.contentType.withMediaType(MediaTypes.`application/json`), f.data))))
          ~> addHeader(RawHeader("Authorization", "123123123123123"))
          ~> sendReceive
          ~> unmarshal[Company]
        )

      val data = """{"name": "test company" , "address": "test address"}"""
      val request = Post("http://localhost:8080/company", data)
      val response: Future[Company] = pipeline(request)
      val res = Await.result(response, timeout.duration)
      assert(res.id.nonEmpty, "Id should be added after creating")
    }




  it should "be able to retrieve company" in {

    val pipeline: HttpRequest => Future[Company] = (
      // we want to get json
      ((_:HttpRequest).mapEntity( _.flatMap( f => HttpEntity(
        f.contentType.withMediaType(MediaTypes.`application/json`),f.data))))
        ~> addHeader(RawHeader("Authorization", "123123123123123"))
        ~> sendReceive
        ~> unmarshal[Company]
      )

    val request = Get("http://localhost:8080/company/1")
    val response: Future[Company] = pipeline(request)
    val res = Await.result(response, timeout.duration)
    assert(res.id.nonEmpty, "Id shouldn't be empty")
  }

  it should "answer with 404 when retrieving non existing company" in {

    val pipeline: HttpRequest => Future[HttpResponse] = (
      // we want to get json
      ((_:HttpRequest).mapEntity( _.flatMap( f => HttpEntity(
        f.contentType.withMediaType(MediaTypes.`application/json`),f.data))))
        ~> addHeader(RawHeader("Authorization", "123123123123123"))
        ~> sendReceive
      )

    val request = Get("http://localhost:8080/company/2")
    val response: Future[HttpResponse] = pipeline(request)
    val res = Await.result(response, timeout.duration)
    assert(res.status.intValue == 404, "status code should be 404 when retrieving non existing entity")
  }

  it should "be able to retrieve all companies" in {

    val pipeline: HttpRequest => Future[List[Company]] = (
      // we want to get json
      ((_:HttpRequest).mapEntity( _.flatMap( f => HttpEntity(
        f.contentType.withMediaType(MediaTypes.`application/json`),f.data))))
        ~> addHeader(RawHeader("Authorization", "123123123123123"))
        ~> sendReceive
        ~> unmarshal[List[Company]]
      )

    val request = Get("http://localhost:8080/company")
    val response: Future[List[Company]] = pipeline(request)
    val res = Await.result(response, timeout.duration)
    assert(res.size == 2, "Id shouldn't be empty")
  }

  it should "be able to delete company" in {

    val pipeline: HttpRequest => Future[HttpResponse] = (
      // we want to get json
      ((_:HttpRequest).mapEntity( _.flatMap( f => HttpEntity(
        f.contentType.withMediaType(MediaTypes.`application/json`),f.data))))
        ~> addHeader(RawHeader("Authorization", "123123123123123"))
        ~> sendReceive
      )

    val request = Delete("http://localhost:8080/company/1")
    val response: Future[HttpResponse] = pipeline(request)
    val res = Await.result(response, timeout.duration)
    assert(res.status.intValue == 200, "Response should be ok")
  }

  it should "should answer with 404 when deleting non existing entity" in {

    val pipeline: HttpRequest => Future[HttpResponse] = (
      // we want to get json
      ((_:HttpRequest).mapEntity( _.flatMap( f => HttpEntity(
        f.contentType.withMediaType(MediaTypes.`application/json`),f.data))))
        ~> addHeader(RawHeader("Authorization", "123123123123123"))
        ~> sendReceive
      )

    val request = Delete("http://localhost:8080/company/2")
    val response: Future[HttpResponse] = pipeline(request)
    val res = Await.result(response, timeout.duration)
    assert(res.status.intValue == 404, "Response should be 404 because company doesnt exist")
  }


  it should "be able to update company" in {

    val pipeline: HttpRequest => Future[HttpResponse] = (
      // we want to get json
      ((_:HttpRequest).mapEntity( _.flatMap( f => HttpEntity(
        f.contentType.withMediaType(MediaTypes.`application/json`),f.data))))
        ~> addHeader(RawHeader("Authorization", "123123123123123"))
        ~> sendReceive
      )

    val data = """{"name": "trata", "address": "qqqqqqqq"}"""
    val request = Put("http://localhost:8080/company/1", data)
    val response: Future[HttpResponse] = pipeline(request)
    val res = Await.result(response, timeout.duration)
    assert(res.status.intValue == 200, "Response should be ok")
  }

  it should "should answer with 404 when updating non existing entity" in {

    val pipeline: HttpRequest => Future[HttpResponse] = (
      // we want to get json
      ((_:HttpRequest).mapEntity( _.flatMap( f => HttpEntity(
        f.contentType.withMediaType(MediaTypes.`application/json`),f.data))))
        ~> addHeader(RawHeader("Authorization", "123123123123123"))
        ~> sendReceive
      )

    val data = """{"name": "trata", "address": "qqqqqqqq"}"""
    val request = Put("http://localhost:8080/company/2", data)
    val response: Future[HttpResponse] = pipeline(request)
    val res = Await.result(response, timeout.duration)
    assert(res.status.intValue == 404, "Response should be 404 because company doesnt exist")
  }

  it should "be able to patch company" in {

    val pipeline: HttpRequest => Future[HttpResponse] = (
      // we want to get json
      ((_:HttpRequest).mapEntity( _.flatMap( f => HttpEntity(
        f.contentType.withMediaType(MediaTypes.`application/json`),f.data))))
        ~> addHeader(RawHeader("Authorization", "123123123123123"))
        ~> sendReceive
      )

    val data = """[{"op": "replace", "path": "/name", "value": "trata"}]"""
    val request = Patch("http://localhost:8080/company/1", data)
    val response: Future[HttpResponse] = pipeline(request)
    val res = Await.result(response, timeout.duration)
    assert(res.status.intValue == 200, "Response should be ok")
  }

  it should "should answer with 404 when patching non existing entity" in {

    val pipeline: HttpRequest => Future[HttpResponse] = (
      // we want to get json
      ((_:HttpRequest).mapEntity( _.flatMap( f => HttpEntity(
        f.contentType.withMediaType(MediaTypes.`application/json`),f.data))))
        ~> sendReceive
      )

    val data = """[{"op": "replace", "path": "/name", "value": "trata"}]"""
    val request = Patch("http://localhost:8080/company/2", data)
    val response: Future[HttpResponse] = pipeline(request)
    val res = Await.result(response, timeout.duration)
    assert(res.status.intValue == 404, "Response should be 404 because company doesnt exist")
  }

  override def beforeAll() = {
    rest.start()
    Thread.sleep(1000)
  }

  override def afterAll() = {
    rest.stop()
  }

}
