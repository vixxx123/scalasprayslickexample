package com.vixxx123.scalasprayslickexample.entity

/**
 * Created by WiktorT on 24/04/2015.
 */
trait EntityHelper {

  def entityUri(resourceBasePath: String, entity: BaseEntity) = {
    s"$resourceBasePath/${entity.getId}"
  }
}
