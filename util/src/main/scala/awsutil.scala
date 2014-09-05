package com.gravitydev.awsutil

import com.amazonaws.handlers.AsyncHandler
import com.amazonaws.AmazonWebServiceRequest
import scala.concurrent.{Future, Promise}

object `package` {

  private class AwsAsyncPromiseHandler [R<:AmazonWebServiceRequest,T] (promise: Promise[T]) extends AsyncHandler [R,T] {
    def onError (ex: Exception) = promise failure ex
    def onSuccess (r: R, x: T) = promise success x
  }

  @deprecated("Just use awsToScala", "0.0.2-SNAPSHOT")
  def withAsyncHandler [R<:AmazonWebServiceRequest,T] (fn: AsyncHandler[R,T] => Unit): Future[T] = { 
    val p = Promise[T]
    fn( new AwsAsyncPromiseHandler(p) )
    p.future
  }

  def awsToScala [R<:AmazonWebServiceRequest,T](fn: Function2[R,AsyncHandler[R,T],java.util.concurrent.Future[T]]): Function1[R,Future[T]] = {req =>
    val p = Promise[T]
    fn(req, new AwsAsyncPromiseHandler(p) )
    p.future
  }
}

