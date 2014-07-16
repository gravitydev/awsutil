import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import com.gravitydev.appenders.AwsLogsAppender
import ch.qos.logback.classic.spi.ILoggingEvent

class AwsLogsAppenderSpec extends FlatSpec with ShouldMatchers {
  "Appender" should "work" in {
    val appender = new AwsLogsAppender(
      awsKey = "XXXXXXXXXXXXX",
      awsSecret = "XXXXXXXXXXXXX",
      logGroup = "loggroup1",
      logStream = "logstream1"
    )

    appender.start()

    for (i <- 1 to 10) {
      appender.doAppend(
        new ILoggingEvent () {
          def getArgumentArray(): Array[Object] = ???
          def getCallerData(): Array[StackTraceElement] = ???
          def getFormattedMessage(): String = ???
          def getLevel(): ch.qos.logback.classic.Level = ???
          def getLoggerContextVO(): ch.qos.logback.classic.spi.LoggerContextVO = ???
          def getLoggerName(): String = "com.gravitydev.appenders.SomeLogger"
          def getMDCPropertyMap(): java.util.Map[String,String] = ???
          def getMarker(): org.slf4j.Marker = ???
          def getMdc(): java.util.Map[String,String] = ???
          def getMessage(): String = "Some message " + i + "\nsome more lines \n laksdjfl asdjfl jlasdjf lsjdf \nsdjkflsdfl jlsdjf lajsd fsdf \n sljdljfals dfjdlf"
          def getThreadName(): String = ???
          def getThrowableProxy(): ch.qos.logback.classic.spi.IThrowableProxy = ???
          def getTimeStamp(): Long = new java.util.Date().getTime
          def hasCallerData(): Boolean = ???
          def prepareForDeferredProcessing(): Unit = ???
        }
      )
    }
  }
}

