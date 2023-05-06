package logger


class ConsolePrintLogger(logLevel: LogLevel) extends Logger(logLevel) {
  override def logAfterLvlCheck(msg: String, logLevel: LogLevel = defaultLogLvl): Unit = {
    println(msg)
  }
}
