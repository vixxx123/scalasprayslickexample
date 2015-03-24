package com.vixxx123.util


object SqlUtil {

  def patch2updateStatement(table: String, jsonParams: String): String = {
    val params = JsonUtil.parseJson[Map[String, Any]](jsonParams)
    val sb = new StringBuilder
    sb.append(s"Update $table SET ")
    var joinStr = ""
    params.foreach {
      item =>
        sb.append(s"$joinStr ${item._1} = ${wrapValue(item._2)}")
        joinStr = ","
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
