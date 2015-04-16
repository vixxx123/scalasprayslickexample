package com.vixxx123.scalasprayslickexample.rest.company

import com.vixxx123.scalasprayslickexample.rest.RestException
import spray.http.StatusCodes

class PersonAlreadyExists(msg: String) extends RestException(StatusCodes.Conflict, msg)
