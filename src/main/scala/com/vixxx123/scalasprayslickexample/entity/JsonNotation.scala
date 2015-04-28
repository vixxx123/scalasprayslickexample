package com.vixxx123.scalasprayslickexample.entity

/**
 * Created by WiktorT on 28/04/2015.
 */
case class JsonNotation(op: String, path: String, value: Option[String]){

  def getSolidOperation: Operation = op match {
    case Operation.Replace => Replace
    case Operation.Remove => Remove
    case _: String => ???
  }
}

sealed class Operation(val op: String)

object Operation {
  val Remove = "remove"
  val Replace = "replace"
}

case object Replace extends Operation(Operation.Replace)
case object Remove extends Operation(Operation.Remove)

