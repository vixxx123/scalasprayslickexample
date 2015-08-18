/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.exampleapi.company

import akka.actor.ActorContext
import akka.routing.RoundRobinPool
import com.vixxx123.scalasprayslickexample.entity.JsonNotation
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.auth.Authorization
import com.vixxx123.scalasprayslickexample.rest.{Api, BaseResourceApi}
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._


/**
 * Company API main class
 *
 * trait HttpService - for spray routing
 * trait BaseResourceApi - for initialization
 * trait DatabaseAccess - for db access
 *
 */
class CompanyApi(val actorContext: ActorContext, companyDao: CompanyDao, override val authorization: Authorization)
  extends BaseResourceApi with Logging {


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
      auth {
        implicit userAuth => {
          pathEnd {
            get {
              ctx => companyGetHandler ! GetMessage(ctx, None)
            } ~
              post {
                entity(as[Company]) {
                  company =>
                    ctx => companyCreateHandler ! CreateMessage(ctx, company)
                }
              }
          } ~
          pathPrefix(IntNumber) {
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
                  entity(as[List[JsonNotation]]) { patch =>
                    ctx => companyPutHandler ! PatchMessage(ctx, patch, entityId)
                  }
                }
              }
            }
          }
        }
      }
    }

}

class CompanyApiBuilder extends Api{
  override def create(actorContext: ActorContext, authorization: Authorization): BaseResourceApi = {
    new CompanyApi(actorContext, new CompanyDao, authorization)
  }
}
