package logger

import LogLevel._

import java.time.Duration

abstract class LoggerInstance(lvlThreshold: LogLevel){
  protected val defaultLogLvl: LogLevel = LogLevel.DEBUG

  protected def logAfterLvlCheck(msg: String, logLvl: LogLevel): Unit

  private def logAfterLvlCheckWrapMsg(msg: String, logLevel: LogLevel): Unit = {
    logAfterLvlCheck(logLevel.toString + ": " +  msg, logLevel)
  }

  private def shouldLog(logLvl: LogLevel, threshold: LogLevel): Boolean = {
    threshold <= logLvl
  }

  def log(msg: String, logLvl: LogLevel = defaultLogLvl): Unit = {
    if(shouldLog(logLvl, lvlThreshold)) logAfterLvlCheckWrapMsg(msg, logLvl)
  }



  def logGetCtx(msg: String, logLvl: LogLevel = defaultLogLvl): LogContext = {
    log(msg, logLvl)
    LogContext()
  }

  def logWithContext(msg: String, ctx: LogContext, logLvl: LogLevel = defaultLogLvl,
                     timingSetting: (String, Duration => Long) = TimingSettings.secondsSetting): LogContext = {
    if(shouldLog(logLvl, lvlThreshold)) logWithContextAfterLvlCheck(msg, ctx, logLvl, timingSetting)
    else LogContext()
  }

  private def logWithContextAfterLvlCheck(msg: String, context: LogContext, level: LogLevel,
                                          timingSetting: (String, Duration => Long)): LogContext = {
    val newCtx = LogContext()
    val timeDelta = newCtx.timeDelta(context)
    val enrichedMsg = s"$msg | Time delta from last timed log (${timingSetting._1}): ${timingSetting._2(timeDelta)} | Now: ${newCtx.timeStamp.toString}"
    logAfterLvlCheckWrapMsg(enrichedMsg, level)
    newCtx
  }




}


