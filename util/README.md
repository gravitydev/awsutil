awsutil
=======

Very small and basic utility to make it more convenient to use AWS java sdk asynchronously in scala. The only dependency is the AWS Java SDK.

Motivation
----------

Amazon AWS sdk comes with async variants of most of their methods, but they return a java Future (pretty useless) instead of a scala Future.
There's no easy or clean way to turn that into a scala Future (much more useful). Fortunately, their methods usually also include a version
that takes an instance of AsyncHandler which this utility uses to create a scala version of the method. 

Essentially, this utility turns this:
```scala
// some async aws sdk method
(R<:AmazonWebServiceRequest, AsyncHandler[R,T]) => java.util.concurrent.Future[T]
```
into:
```scala
R<:AmazonWebServiceRequest => scala.concurrent.Future[T]
```

Installation
------------

Add this to build.sbt:
```sbt
resolvers += "gravity" at "https://devstack.io/repo/gravitydev/public"

libraryDependencies += "com.gravitydev" %% "awsutil" % "0.0.1-SNAPSHOT"
```

Use it like this:
```scala
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model._
import com.gravitydev.awsutil.awsToScala
... 
// regular amazon request (dynamodb for this example)
val req = new GetItemRequest()
  .withTableName("users")
  .withKey("userid@somedomain.com")
  .withAttributesToGet(...)

// wrap the relevant '...Async' call with 'awsToScala' to convert the aws call from:
// someAwsMethod(request, callback) => unit 
// into a scala future
// type: Future[BatchGetItemResult]
val response = withAsyncHandler[BatchGetItemRequest,BatchGetItemResult](client.batchGetItemAsync(req, _))

// do what you want with it now, asynchronously:
for (res <- response) yield {
  ...
}
```
