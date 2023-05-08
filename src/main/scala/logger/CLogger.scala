package logger

import scala.annotation.unused
import scala.concurrent.duration.Duration

object CLogger {

  private var logger: LoggerInstance = new ConsolePrintLogger(LogLevel.INFO)

  def setLogger(newLogger: LoggerInstance): LoggerInstance = {
    logger = newLogger
    logger
  }

  def getLogger: LoggerInstance = logger


  def timeExpression[A](msg: String, logLevel: LogLevel = LogLevel.DEBUG)(exp: => A): A = {
    val lc  = logger.logGetCtx(msg + " -- Start", logLevel)
    val r: A = exp
    logger.logWithContext(msg + " -- End", lc, logLevel, TimingSettings.millisSetting)

    r
  }

}
