package com.vixxx123.scalasprayslickexample.exampleapi.person

import akka.actor.{ActorContext, Props}
import akka.routing.RoundRobinPool
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.{BaseResourceApi, Api}
import spray.httpx.SprayJsonSupport._

import scala.slick.jdbc.meta.MTable

/**
 * Person API main class
 *
 * trait HttpService - for spray routing
 * trait BaseResourceApi - for initialization
 * trait DatabaseAccess - for db access
 *
 */
class PersonApi(actorContext: ActorContext) extends BaseResourceApi with Logging {

  val personCreateHandler = actorContext.actorOf(RoundRobinPool(2).props(Props[CreateActor]), s"${ResourceName}CreateRouter")
  val personPutHandler = actorContext.actorOf(RoundRobinPool(5).props(Props[UpdateActor]), s"${ResourceName}PutRouter")
  val personGetHandler = actorContext.actorOf(RoundRobinPool(20).props(Props[GetActor]), s"${ResourceName}GetRouter")
  val personDeleteHandler = actorContext.actorOf(RoundRobinPool(20).props(Props[DeleteActor]), s"${ResourceName}DeleteRouter")

  override val logTag: String = getClass.getName

  override def init() = {
    PersonDb.initTable()
    super.init()
  }

  override def route() =
    pathPrefix(ResourceName) {
      pathEnd {
        get {
          ctx => personGetHandler ! GetMessage(ctx, None)
        } ~
        post {
          entity(as[Person]) {
            entity =>
              ctx => personCreateHandler ! CreateMessage(ctx, entity)
          }
        }
      } ~
      pathPrefix (IntNumber){
        entityId => {
          pathEnd {
            get {
              ctx => personGetHandler ! GetMessage(ctx, Some(entityId))
            } ~ put {
              entity(as[Person]) { entity =>
                ctx => personPutHandler ! PutMessage(ctx, entity.copy(id = Some(entityId)))
              }
            } ~ delete {
              ctx => personDeleteHandler ! DeleteMessage(ctx, entityId)
            } ~ patch {
              ctx => personPutHandler ! PatchMessage(ctx, entityId)
            }
          }
        }
      }
    }
}

object PersonApi extends Api{
  override def create(actorContext: ActorContext): BaseResourceApi = new PersonApi(actorContext)
}