package logger

import java.time.Duration

object TimingSettings {

  val nanosSetting: (String, Duration => Long) = "nanos" -> ((d: Duration) => d.toNanos)
  val secondsSetting: (String, Duration => Long) = "secs" -> ((d: Duration) => d.toSeconds)
  val millisSetting: (String, Duration => Long) = "millis" -> ((d: Duration) => d.toMillis)

}
