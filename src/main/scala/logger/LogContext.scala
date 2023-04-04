package logger

import java.time.{Duration, LocalDateTime}

case class LogContext(timeStamp: LocalDateTime = LocalDateTime.now()){
  def timeDelta(startCtx: LogContext): Duration = {
    Duration.between(startCtx.timeStamp, timeStamp)
  }
}
