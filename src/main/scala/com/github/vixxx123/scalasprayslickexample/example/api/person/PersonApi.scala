/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.github.vixxx123.scalasprayslickexample.example.api.person

import akka.actor.ActorContext
import akka.routing.RoundRobinPool
import com.github.vixxx123.scalasprayslickexample.entity.JsonNotation
import com.github.vixxx123.scalasprayslickexample.logger.Logging
import com.github.vixxx123.scalasprayslickexample.rest.{BaseResourceBuilder, BaseResource}
import com.github.vixxx123.scalasprayslickexample.rest.auth.{RestApiUser, Authorization}
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._


/**
 * Person API main class
 *
 * trait HttpService - for spray routing
 * trait BaseResourceApi - for initialization
 *
 */
class PersonApi(val actorContext: ActorContext, personDao: PersonDao, override val authorization: Authorization)
  extends BaseResource with Logging {

  val personCreateHandler = actorContext.actorOf(RoundRobinPool(2).props(CreateActor.props(personDao)), CreateActor.Name)
  val personPutHandler = actorContext.actorOf(RoundRobinPool(5).props(UpdateActor.props(personDao)), UpdateActor.Name)
  val personGetHandler = actorContext.actorOf(RoundRobinPool(20).props(GetActor.props(personDao)), GetActor.Name)
  val personDeleteHandler = actorContext.actorOf(RoundRobinPool(2).props(DeleteActor.props(personDao)), DeleteActor.Name)

  override val logTag: String = getClass.getName

  override def init() = {
    personDao.initTable()
    super.init()
  }

  override def authorisedResource = false

  override def route(implicit restApiUser: RestApiUser) =
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
              entity(as[List[JsonNotation]]) { patch =>
                ctx => personPutHandler ! PatchMessage(ctx, patch, entityId)
              }
            }
          }
        }
      }
    }
}

class PersonApiBuilder extends BaseResourceBuilder{
  override def create(actorContext: ActorContext, authorization: Authorization): BaseResource = {
    new PersonApi(actorContext, new PersonDao, authorization)
  }
}