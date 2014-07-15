awsutil
=======

Very small and basic utility to make it more convenient to use AWS java sdk asynchronously in scala.

Usage
-----

Add this to build.sbt:
```sbt
libraryDependencies += "com.gravitydev" %% "awsutil" % "0.0.1-SNAPSHOT"
```

Use it like this:
```scala
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model._
import com.gravitydev.awsutil.withAsyncHandler
... 
// regular amazon request (dynamodb for this example)
val req = new GetItemRequest()
  .withTableName("users")
  .withKey("userid@somedomain.com")
  .withAttributesToGet(...)

// wrap the relevant '...Async' call with 'withAsyncHandler' to convert the aws call from:
// someAwsMethod(request, callback) => unit 
// into a scala future
// type: Future[BatchGetItemResult]
val response = withAsyncHandler[BatchGetItemRequest,BatchGetItemResult](client.batchGetItemAsync(req, _))

// do what you want with it now, asynchronously:
for (res <- response) yield {
  ...
}
```
