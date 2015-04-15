package com.vixxx123.logger

sealed trait LogLevel
case object DebugLevel extends LogLevel
case object InfoLevel extends LogLevel
case object ErrorLevel extends LogLevel
