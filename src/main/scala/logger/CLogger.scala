package logger

object CLogger {

  private var logger: LoggerInstance = new ConsolePrintLogger(LogLevel.INFO)

  def setLogger(newLogger: LoggerInstance): LoggerInstance = {
    logger = newLogger
    logger
  }

  def getLogger: LoggerInstance = logger

}
