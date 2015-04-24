package com.vixxx123.scalasprayslickexample.integration

import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorSystem}
import akka.util.Timeout
import com.vixxx123.scalasprayslickexample.exampleapi.company.{Company, CreateMessage, CreateActor}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FlatSpec

import org.mockito.Mockito._
import org.mockito.Matchers._


/**
 * Created by Wiktor Tychulski on 2015-04-24.
 */
@RunWith(classOf[JUnitRunner])
class CreateActorSpec extends FlatSpec with Mocking {

  implicit val system = ActorSystem("test")

  val createActor = system.actorOf(CreateActor.props(companyDb), CreateActor.Name)
  implicit val timeout = Timeout(2000, TimeUnit.SECONDS)

  "Create actor" should "be able to create new company" in {
    createActor ! CreateMessage(ctx, Company(None, "name", "address"))
    Thread.sleep(1000)
    verify(ctx, times(1)).complete(isA(classOf[Company]))(any())
  }

//  override def beforeAll() = {
//
//  }

  case object Init

  class Temp extends Actor {
    override def receive: Receive = {
      case Init =>

    }
  }
}
