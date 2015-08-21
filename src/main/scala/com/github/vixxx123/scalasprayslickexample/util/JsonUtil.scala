/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */


package com.github.vixxx123.scalasprayslickexample.util

import java.io.StringWriter
import java.text.SimpleDateFormat

import com.fasterxml.jackson.core.JsonParser.Feature
import com.fasterxml.jackson.databind.{DeserializationFeature, ObjectMapper, SerializationFeature}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.reflect.ClassTag

/**
 * Json utils
 */
object JsonUtil {

  private val ErrorMessage = "Error during json parsing: "
  private val mapper = new ObjectMapper()
  mapper.enable(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS)
  mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS)
  mapper.configure(Feature.ALLOW_COMMENTS, true)
  mapper.registerModule(DefaultScalaModule)
  mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
  mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"))

  def serialize(value: Any): String = {
    val writer = new StringWriter()
    mapper.writeValue(writer, value)
    writer.toString
  }
}

