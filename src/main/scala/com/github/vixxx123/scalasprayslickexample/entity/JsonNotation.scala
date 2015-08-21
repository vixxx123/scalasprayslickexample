/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.github.vixxx123.scalasprayslickexample.entity

import com.github.vixxx123.scalasprayslickexample.rest.UpdateException

/**
 * Represents patch operation object
 *
 * @param op
 * @param path
 * @param value
 */
case class JsonNotation(op: String, path: String, value: Option[String]) {

  def getSolidOperation: Operation = op match {
    case Operation.Replace => Replace
    case Operation.Remove => Remove
    case _: String => throw new UpdateException("patch operation not implemented " + op)
  }
}

sealed class Operation(val op: String)

object Operation {
  val Remove = "remove"
  val Replace = "replace"
}

case object Replace extends Operation(Operation.Replace)
case object Remove extends Operation(Operation.Remove)

