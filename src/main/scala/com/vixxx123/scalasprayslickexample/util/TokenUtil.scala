package com.vixxx123.scalasprayslickexample.util

import java.security.{MessageDigest, SecureRandom}

/**
 * Created by wiktort on 11/05/2015.
 *
 * Created on 11/05/2015
 */
object TokenUtil {

  lazy val random = SecureRandom.getInstance("SHA1PRNG")

  val hexChars = "0123456789ABCDEF"

  def generateToken: String = {
    val bytes = Array.fill(32)(0.byteValue)
    random.nextBytes(bytes)
    sha1(bytes)
  }

  private def sha1(bytes: Array[Byte]): String = digest(bytes, MessageDigest.getInstance("SHA1"))

  private def digest(bytes: Array[Byte], md: MessageDigest): String = {
    md.update(bytes)
    hexify(md.digest)
  }

  private def hexify(bytes: Array[Byte]): String = {
    val builder = new java.lang.StringBuilder(bytes.length * 2)
    bytes.foreach { byte => builder.append(hexChars.charAt((byte & 0xF0) >> 4)).append(hexChars.charAt(byte & 0xF)) }
    builder.toString
  }
}
