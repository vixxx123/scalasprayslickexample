/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.exampleapi.company

import java.util.concurrent.TimeUnit

import akka.pattern.ask
import akka.actor.ActorContext
import akka.routing.RoundRobinPool
import akka.util.Timeout
import com.vixxx123.scalasprayslickexample.entity.JsonNotation
import com.vixxx123.scalasprayslickexample.logger.Logging
import com.vixxx123.scalasprayslickexample.rest.outh2._
import com.vixxx123.scalasprayslickexample.rest.{Api, BaseResourceApi}
import spray.httpx.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._
import spray.routing.{Directive1, AuthenticationFailedRejection}
import spray.routing.AuthenticationFailedRejection.CredentialsRejected
import spray.routing.authentication.Authentication
import spray.routing.directives.AuthMagnet

import scala.concurrent.{Promise, ExecutionContext, Future}
import scala.util.{Failure, Success}


/**
 * Company API main class
 *
 * trait HttpService - for spray routing
 * trait BaseResourceApi - for initialization
 * trait DatabaseAccess - for db access
 *
 */
class CompanyApi(actorContext: ActorContext, companyDao: CompanyDao) extends BaseResourceApi with Logging {

  implicit val ec = actorContext.dispatcher

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

  def validate(token: String):  AuthMagnet[AuthUser] = {
    val promise = Promise[Authentication[AuthUser]]()
    implicit val timeout = Timeout(1, TimeUnit.SECONDS)


    (SessionService.getSessionManager ? new GetSession(token)).mapTo[Session].onComplete{
      case Success(session) =>
        promise success Right(session.user)
      case Failure(e) =>
        promise success Left(AuthenticationFailedRejection(CredentialsRejected, List.empty))
    }

    new AuthMagnet(onSuccess(promise.future))
  }

  val authenticator = TokenAuthenticator[AuthUser](
    headerName = "access_token",
    queryStringParameterName = "access_token"
    ) { key =>
      val promise = Promise[Option[AuthUser]]()
      implicit val timeout = Timeout(1, TimeUnit.SECONDS)
      (SessionService.getSessionManager ? new GetSession(key)).mapTo[Session].onComplete{
        case Success(session) =>
          println(session)
          promise success Some(session.user)
        case Failure(e) =>
          println(e)
          promise success None
      }
      promise.future
    }

  def auth: Directive1[AuthUser] = authenticate(authenticator)

  override def route() =
    pathPrefix(ResourceName) {
      auth {
            user => {

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
  override def create(actorContext: ActorContext): BaseResourceApi = {
    new CompanyApi(actorContext, new CompanyDao)
  }
}
