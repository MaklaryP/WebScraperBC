package logger

abstract class LogLevel(protected val value: Int) extends Ordered[LogLevel] {
  override def compare(that: LogLevel): Int = {
    value - that.value
  }

  override def <=(that: LogLevel): Boolean = {
    compare(that) <= 0
  }

  type LogLevelType = LogLevel
}

object LogLevel{
case object DEBUG extends LogLevel(5)
case object INFO extends LogLevel(10)
case object WARN extends LogLevel(15)
case object ERROR extends LogLevel(20)
}


