/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.rest

import spray.http.StatusCodes


class RestException(val code: StatusCodes.ClientError, val msg: String) extends Exception(msg)

case class EntityNotFound(override val msg: String) extends RestException(StatusCodes.NotFound, msg)

case class UpdateException(override val msg: String) extends RestException(StatusCodes.BadRequest, msg)
