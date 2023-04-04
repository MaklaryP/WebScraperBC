package logger


class ConsolePrintLogger(logLevel: LogLevel) extends Logger(logLevel) {
  override def logAfterLvlCheck(msg: String, logLevel: LogLevel = defaultLogLvl): Unit = {
    println(msg)
  }

  override def logWithContextAfterLvlCheck(msg: String, ctx: LogContext, logLevel: LogLevel = defaultLogLvl): LogContext = {
    val newCtx = LogContext()
    val timeDelta = newCtx.timeDelta(ctx)
    println(s"$msg | Time delta from last timed log: ${timeDelta.toSeconds} | Now: ${newCtx.timeStamp.toString}")

    newCtx
  }
}
