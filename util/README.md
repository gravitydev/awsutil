awsutil
=======

Very small and basic utility to make it more convenient to use AWS java sdk asynchronously in scala. The only dependency is the AWS Java SDK.

Motivation
----------

Amazon AWS sdk comes with async variants of most of their methods, but they return a java Future (pretty useless) instead of a scala Future.
There's no easy or clean way to turn that into a scala Future. Fortunately, their methods usually also include a version
that takes an instance of AsyncHandler, which this utility uses to create a scala version of the method. 

Essentially, this utility turns this:
```scala
// some async aws sdk method:
// (R<:AmazonWebServiceRequest, AsyncHandler[R,T]) => java.util.concurrent.Future[T]
client.someMethodAsync(request, handler): Future[T] // java future
```
into:
```scala
// R<:AmazonWebServiceRequest => Future[T] // scala future
awsToScala(client.someMethodAsync)(request): Future[T] // scala future
```

Installation
------------
It's really just a method, you can look at the source and copy it. Or use this if you want:

```sbt
resolvers += "gravity" at "https://devstack.io/repo/gravitydev/public"

libraryDependencies += "com.gravitydev" %% "awsutil" % "0.0.2-SNAPSHOT"
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
val response = awsToScala(client.batchGetItemAsync)(req)

// do what you want with it now, asynchronously:
// response is a regular scala Future[BatchGetItemResult]
for (res <- response) yield {
  ...
}
```

You can use this with most (all?) of the Amazon AWS SDK methods suffixed with "...Async".
