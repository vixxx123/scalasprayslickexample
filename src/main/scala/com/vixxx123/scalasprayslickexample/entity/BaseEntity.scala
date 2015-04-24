package com.vixxx123.scalasprayslickexample.entity

/**
 * Created by WiktorT on 24/04/2015.
 */
trait BaseEntity {
  val id: Option[Int]
  def getId: Int
}
