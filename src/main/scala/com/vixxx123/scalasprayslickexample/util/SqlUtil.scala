/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */

package com.vixxx123.scalasprayslickexample.util

import com.vixxx123.scalasprayslickexample.entity.{Remove, Replace, JsonNotation}
import com.vixxx123.scalasprayslickexample.rest.UpdateException

/**
 * Sql utils
 */
object SqlUtil {

  def patch2updateStatement(table: String, patch: JsonNotation): String = {
    val sb = new StringBuilder

    patch.getSolidOperation match {
      case Replace =>
        sb.append(s"Update $table SET ")
        sb.append(s"${patch.path.substring(1)} = ${wrapValue(patch.value.get)}")

      case Remove =>
        sb.append(s"Update $table SET ")
        sb.append(s"${patch.path.substring(1)} = NULL")

      case _: Any =>
        throw new UpdateException("Not supported operation: " + patch.op)
    }

    sb.toString()
  }

  private def wrapValue(value: Any): String = {
    value match {
      case Int => s"$value"
      case _: Any => s""""$value""""
    }
  }

  def whereById(id: Int):String = {
    s"WHERE id = $id"
  }
}
