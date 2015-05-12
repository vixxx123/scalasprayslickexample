/**
 * Created by Wiktor Tychulski on 2015-04-24.
 *
 * Created on 2015-04-24
 */
package com.vixxx123.scalasprayslickexample.entity

trait BaseEntity {
  val id: Option[Int]
  def getId: Int = id.getOrElse(throw new Exception("Getting id of not persisted entity"))
}
