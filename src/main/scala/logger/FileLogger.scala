package logger

import java.io.{BufferedWriter, FileWriter}
import java.nio.file.Path
import java.util.logging.{ConsoleHandler, FileHandler, LogRecord, SimpleFormatter, Logger => JLogger}

class FileLogger(logLevel: LogLevel, private val path: Path) extends LoggerInstance(logLevel) {
  private val logger = JLogger.getLogger("FileLogger")

  private val myFormatter = new SimpleFormatter() {
    override def format(record: LogRecord): String = {
      record.getMessage + "\n"
    }
  }

  private val fileHandler = new FileHandler(path.toFile.getAbsolutePath)
  fileHandler.setFormatter(myFormatter)
  logger.addHandler(fileHandler)

  override protected def logAfterLvlCheck(msg: String, logLvl: LogLevel): Unit = {
    logger.info(msg)
  }
}
