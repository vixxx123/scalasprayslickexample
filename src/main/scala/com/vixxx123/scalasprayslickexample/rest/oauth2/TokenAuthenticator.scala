package com.vixxx123.scalasprayslickexample.rest.oauth2

import scala.concurrent.{ExecutionContext, Future}
import spray.routing.{AuthenticationFailedRejection, RequestContext}
import spray.routing.authentication.{Authentication, ContextAuthenticator}

/** Token based authentication for Spray Routing.
  *
  * Extracts an API key from the header or querystring and authenticates requests.
  *
  * TokenAuthenticator[T] takes arguments for the named header/query string containing the API key and
  * an authenticator that returns an Option[T]. If None is returned from the authenticator, the request
  * is rejected.
  *
  * Usage:
  *
  *     val authenticator = TokenAuthenticator[User](
  *       headerName = "My-Api-Key",
  *       queryStringParameterName = "api_key"
  *     ) { key =>
  *       User.findByAPIKey(key)
  *     }
  *
  *     def auth: Directive1[User] = authenticate(authenticator)
  *
  *     val home = path("home") {
  *       auth { user =>
  *         get {
  *           complete("OK")
  *         }
  *       }
  *     }
  */

object TokenAuthenticator {

  object TokenExtraction {

    type TokenExtractor = RequestContext => Option[String]

    def fromHeader(headerName: String): TokenExtractor = { context: RequestContext =>
      context.request.headers.find(_.name == headerName).map(_.value)
    }

    def fromQueryString(parameterName: String): TokenExtractor = { context: RequestContext =>
      context.request.uri.query.get(parameterName)
    }

  }

  class TokenAuthenticator[T](extractor: TokenExtraction.TokenExtractor, authenticator: (String => Future[Option[T]]))
                             (implicit executionContext: ExecutionContext) extends ContextAuthenticator[T] {

    import AuthenticationFailedRejection._

    def apply(context: RequestContext): Future[Authentication[T]] =
      extractor(context) match {
        case None =>
          Future(
            Left(AuthenticationFailedRejection(CredentialsMissing, List()))
          )
        case Some(token) =>
          authenticator(token) map {
            case Some(t) =>
              Right(t)
            case None =>
              Left(AuthenticationFailedRejection(CredentialsRejected, List()))
          }
      }

  }

  def apply[T](headerName: String, queryStringParameterName: String)(authenticator: (String => Future[Option[T]]))
              (implicit executionContext: ExecutionContext) = {

    def extractor(context: RequestContext) =
      TokenExtraction.fromHeader(headerName)(context) orElse
        TokenExtraction.fromQueryString(queryStringParameterName)(context)

    new TokenAuthenticator(extractor, authenticator)
  }

}