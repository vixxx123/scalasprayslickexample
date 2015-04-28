package com.vixxx123.scalasprayslickexample.rest

import spray.http.StatusCodes


class RestException(val code: StatusCodes.ClientError, val msg: String) extends Exception(msg)

case class EntityNotFound(override val msg: String) extends RestException(StatusCodes.NotFound, msg)

case class UpdateException(override val msg: String) extends RestException(StatusCodes.BadRequest, msg)
