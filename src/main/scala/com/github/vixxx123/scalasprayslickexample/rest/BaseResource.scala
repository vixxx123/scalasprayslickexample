/**
 * Created by wiktort on 19/08/2015.
 *
 * Created on 19/08/2015
 */
package com.github.vixxx123.scalasprayslickexample.rest

import akka.actor.ActorContext
import com.github.vixxx123.scalasprayslickexample.entity.JsonNotation
import com.github.vixxx123.scalasprayslickexample.rest.auth.{NoAuthorisation, Authorization, RestApiUser}
import spray.json.DefaultJsonProtocol._
import spray.routing._

/**
 * Base class for all resources api available via REST
 *
 * implement:
 * route - To specify available route for resource
 * authorisedResource - to specify if accessing resource should be authorized
 *
 */
trait BaseResource extends HttpServiceBase {

  val actorContext: ActorContext
  implicit val ec = actorContext.dispatcher

  /**
   * Patch operation json notation object marshal/unmarshal format
   */
  implicit val PatchJsonNotationFormat = jsonFormat3(JsonNotation)

  def authorisedResource: Boolean

  /**
   * override if resource need to be initialized. For example db table creation etc
   */
  def init(): Unit = {}

  /**
   * Route for resource
   *
   */
  def route(implicit user: RestApiUser): Route

  final private[rest] def apiRoute() = auth { user => route(user) }

  /**
   * Authorization
   */
  def authorization: Authorization

  final private def auth: Directive1[RestApiUser] = {
    val authenticator = authorisedResource match {
      case true => authorization.authenticator
      case false => NoAuthorisation.authenticator
    }

    authenticate(authenticator)
  }
}


trait BaseResourceBuilder {
  def create(actorContext: ActorContext, auth: Authorization): BaseResource
}
