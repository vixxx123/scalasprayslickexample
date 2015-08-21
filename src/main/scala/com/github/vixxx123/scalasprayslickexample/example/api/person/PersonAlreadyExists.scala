/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.github.vixxx123.scalasprayslickexample.example.api.person

import com.github.vixxx123.scalasprayslickexample.rest.RestException
import spray.http.StatusCodes

class PersonAlreadyExists(msg: String) extends RestException(StatusCodes.Conflict, msg)
