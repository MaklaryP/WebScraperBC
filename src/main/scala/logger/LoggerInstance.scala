package logger

import LogLevel._

abstract class LoggerInstance(lvlThreshold: LogLevel){
  protected val defaultLogLvl: LogLevel = LogLevel.DEBUG

  protected def logAfterLvlCheck(msg: String, logLvl: LogLevel): Unit

  private def logAfterLvlCheckWrapMsg(msg: String, logLevel: LogLevel) = {
    logAfterLvlCheck(logLevel.toString + ": " +  msg, logLevel)
  }

  private def shouldLog(logLvl: LogLevel, threshold: LogLevel): Boolean = {
    threshold <= logLvl
  }

  def log(msg: String, logLvl: LogLevel = defaultLogLvl): Unit = {
    if(shouldLog(logLvl, lvlThreshold)) logAfterLvlCheckWrapMsg(msg, logLvl)
  }



  def logGetCtx(msg: String, logLvl: LogLevel = defaultLogLvl): LogContext = {
    log(msg)
    LogContext()
  }

  def logWithContext(msg: String, ctx: LogContext, logLvl: LogLevel = defaultLogLvl): LogContext = {
    if(shouldLog(logLvl, lvlThreshold)) logWithContextAfterLvlCheck(msg, ctx, logLvl)
    else LogContext()
  }

  def logWithContextAfterLvlCheck(msg: String, context: LogContext, level: LogLevel): LogContext = {
    val newCtx = LogContext()
    val timeDelta = newCtx.timeDelta(context)
    val enrichedMsg = s"$msg | Time delta from last timed log: ${timeDelta.toSeconds} | Now: ${newCtx.timeStamp.toString}"
    logAfterLvlCheckWrapMsg(enrichedMsg, level)
    newCtx
  }




}
