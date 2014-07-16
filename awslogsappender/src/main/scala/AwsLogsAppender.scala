package com.gravitydev.appenders

import ch.qos.logback.core.{Layout, UnsynchronizedAppenderBase}
import ch.qos.logback.classic.spi.ILoggingEvent
import akka.actor.{Actor, ActorRef, ActorSystem, Props, FSM}
import com.amazonaws.services.logs.AWSLogsAsyncClient
import com.amazonaws.services.logs.model._
import com.amazonaws.auth.{BasicAWSCredentials, AWSCredentialsProvider}
import com.gravitydev.awsutil.withAsyncHandler
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.collection.JavaConverters._
import scala.beans.BeanProperty

class KeyAndSecretAwsLogsAppender extends AwsLogsAppender {
  @BeanProperty var awsKey: String = _
  @BeanProperty var awsSecret: String = _

  override protected lazy val awsCredentialsProvider = new AWSCredentialsProvider {
    def getCredentials() = new BasicAWSCredentials(awsKey, awsSecret)
    def refresh() = ()
  }
}

class CredentialsProviderAwsLogsAppender extends AwsLogsAppender {
  @BeanProperty var credentialsProvider: AWSCredentialsProvider = _

  override protected lazy val awsCredentialsProvider = credentialsProvider
}

abstract class AwsLogsAppender () extends UnsynchronizedAppenderBase[ILoggingEvent] {
  @BeanProperty var logGroup: String = _
  @BeanProperty var logStream: String = _
  @BeanProperty var layout: Layout[ILoggingEvent] = _

  lazy val system = ActorSystem("awslogsappender")
  
  var logsClient: AWSLogsAsyncClient = _

  var actor: ActorRef = _

  protected def awsCredentialsProvider: AWSCredentialsProvider

  override def start () {
    import system.dispatcher

    logsClient = new AWSLogsAsyncClient(awsCredentialsProvider)

    // find the group or create it, same with stream
    Await.result(
      for {
        groupsRes <- withAsyncHandler[DescribeLogGroupsRequest,DescribeLogGroupsResult](
          logsClient.describeLogGroupsAsync(new DescribeLogGroupsRequest().withLogGroupNamePrefix(logGroup).withLimit(1), _)
        )

        _ <- {
          groupsRes.getLogGroups.asScala.headOption map {_ =>
            Future.successful(())
          } getOrElse {
            withAsyncHandler[CreateLogGroupRequest,java.lang.Void](
              logsClient.createLogGroupAsync(new CreateLogGroupRequest().withLogGroupName(logGroup), _)
            )
          }
        }
        streamsRes <- withAsyncHandler[DescribeLogStreamsRequest,DescribeLogStreamsResult](
          logsClient.describeLogStreamsAsync(new DescribeLogStreamsRequest().withLogGroupName(logGroup).withLogStreamNamePrefix(logStream).withLimit(1), _)
        )
        uploadSequenceToken <- {
          streamsRes.getLogStreams.asScala.headOption map {stream =>
            Future.successful(Some(stream.getUploadSequenceToken))
          } getOrElse {
            withAsyncHandler[CreateLogStreamRequest,java.lang.Void](
              logsClient.createLogStreamAsync(new CreateLogStreamRequest().withLogGroupName(logGroup).withLogStreamName(logStream), _)
            ) map (_ => None)
          }
        }
      } yield {
        actor = system.actorOf(
          Props(classOf[AwsLogsAppenderActor], logsClient, logGroup, logStream, uploadSequenceToken, layout, 100, 20.seconds),
          "awslogsappender"
        )
      },
      5.seconds
    )
     
    super.start()
  }

  def append (ev: ILoggingEvent) {
    actor ! AwsLogsAppenderActor.Add(ev)
  }
}


object AwsLogsAppenderActor {
  private[appenders] sealed trait State
  private case object Empty extends State
  private case object NonEmpty extends State

  private [appenders] sealed trait Command
  private [appenders] case class Add[T] (value: T) extends Command
}

/**
 * Receive events and batch them up for processing
 * Only process them after a maxBatchSize has been reached, or after an interval (whichever comes first)
 */
private class AwsLogsAppenderActor (
  logsClient: AWSLogsAsyncClient,
  logGroup: String,
  logStream: String,
  sequenceToken: Option[String],
  layout: Layout[ILoggingEvent],
  maxBatchSize: Int, 
  maxInterval: FiniteDuration
) extends Actor with FSM[AwsLogsAppenderActor.State,Seq[ILoggingEvent]] {
  import AwsLogsAppenderActor._

  startWith (Empty, Seq.empty[ILoggingEvent])

  when (Empty) {
    case Event(Add(value), messages) => goto(NonEmpty) using Seq(value.asInstanceOf[ILoggingEvent])
  }

  when (NonEmpty, stateTimeout = maxInterval) {
    case Event(Add(value), data) if data.size >= maxBatchSize => stay using data ++ Seq(value.asInstanceOf[ILoggingEvent]) forMax 0.seconds
    case Event(Add(value), data) => stay using data ++ Seq(value.asInstanceOf[ILoggingEvent])
    case Event(StateTimeout, data) => goto(Empty) using Seq.empty[ILoggingEvent]
  }

  onTransition {
    case NonEmpty -> Empty => {
      // send to aws
      val events = for (ev <- stateData) yield {
        new InputLogEvent()
          .withTimestamp(ev.getTimeStamp)
          .withMessage(layout.doLayout(ev))
      }

      val req = new PutLogEventsRequest()
        .withLogGroupName(logGroup)
        .withLogStreamName(logStream)
        .withLogEvents(events.asJava)

      val putReq = sequenceToken map {token => req.withSequenceToken(token)} getOrElse req
       
      withAsyncHandler[PutLogEventsRequest,PutLogEventsResult](logsClient.putLogEventsAsync(putReq, _))
    }
  }

  initialize()
}

