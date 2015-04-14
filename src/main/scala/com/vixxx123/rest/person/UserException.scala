package com.vixxx123.rest.person

import com.vixxx123.rest.RestException
import spray.http.StatusCodes

class UserAlreadyExists(msg: String) extends RestException(StatusCodes.Conflict, msg)
