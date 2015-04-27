package com.vixxx123.scalasprayslickexample.exampleapi.company

import akka.actor.{ActorContext, Props}
import akka.routing.RoundRobinPool
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.{Api, BaseResourceApi}
import spray.httpx.SprayJsonSupport._

/**
 * Company API main class
 *
 * trait HttpService - for spray routing
 * trait BaseResourceApi - for initialization
 * trait DatabaseAccess - for db access
 *
 */
class CompanyApi(actorContext: ActorContext, companyDao: CompanyDao) extends BaseResourceApi with Logging {

  /**
   * Handler val names must be unique in the system - all
   */

  private val companyCreateHandler = actorContext.actorOf(RoundRobinPool(2).props(CreateActor.props(companyDao)), CreateActor.Name)
  private val companyPutHandler = actorContext.actorOf(RoundRobinPool(5).props(UpdateActor.props(companyDao)), UpdateActor.Name)
  private val companyGetHandler = actorContext.actorOf(RoundRobinPool(20).props(GetActor.props(companyDao)), GetActor.Name)
  private val companyDeleteHandler = actorContext.actorOf(RoundRobinPool(2).props(DeleteActor.props(companyDao)), DeleteActor.Name)

  override val logTag: String = getClass.getName

  override def init() = {
    companyDao.initTable()
    super.init()
  }

  override def route() =
    pathPrefix(ResourceName) {
      pathEnd {
        get {
          ctx => companyGetHandler ! GetMessage(ctx, None)
        } ~
        post {
          entity(as[Company]) {
            user =>
              ctx => companyCreateHandler ! CreateMessage(ctx, user)
          }
        }
      } ~
      pathPrefix (IntNumber){
        entityId => {
          pathEnd {
            get {
              ctx => companyGetHandler ! GetMessage(ctx, Some(entityId))
            } ~ put {
              entity(as[Company]) { entity =>
                ctx => companyPutHandler ! PutMessage(ctx, entity.copy(id = Some(entityId)))
              }
            } ~ delete {
              ctx => companyDeleteHandler ! DeleteMessage(ctx, entityId)
            } ~ patch {
              ctx => companyPutHandler ! PatchMessage(ctx, entityId)
            }
          }
        }
      }
    }
}

class CompanyApiBuilder extends Api{
  override def create(actorContext: ActorContext): BaseResourceApi = {
    new CompanyApi(actorContext, new CompanyDao)
  }
}
