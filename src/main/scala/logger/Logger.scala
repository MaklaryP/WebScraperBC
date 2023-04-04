package logger

import LogLevel._

abstract class Logger(lvlThreshold: LogLevel){
  protected val defaultLogLvl: LogLevel.ERROR.type = LogLevel.ERROR

  private def shouldLog(logLvl: LogLevel, threshold: LogLevel): Boolean = {
    threshold <= logLvl
  }

  def log(msg: String, logLvl: LogLevel = defaultLogLvl): Unit = {
    if(shouldLog(logLvl, lvlThreshold)) logAfterLvlCheck(msg, logLvl)
  }

  protected def logAfterLvlCheck(msg: String, logLvl: LogLevel): Unit



  def logGetCtx(msg: String, logLvl: LogLevel = defaultLogLvl): LogContext = {
    log(msg)
    LogContext()
  }

  def logWithContext(msg: String, ctx: LogContext, logLvl: LogLevel = defaultLogLvl): LogContext = {
    if(shouldLog(logLvl, lvlThreshold)) logWithContextAfterLvlCheck(msg, ctx, logLvl)
    else LogContext()
  }

  def logWithContextAfterLvlCheck(msg: String, context: LogContext, level: LogLevel): LogContext

}
