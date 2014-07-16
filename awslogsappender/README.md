awslogsappender
===============

Logback appender that saves logs to AWS Logs.

Usage
-----

Add this to build.sbt:
```sbt
libraryDependencies += "com.gravitydev" %% "awslogsappender" % "0.0.1-SNAPSHOT"
```

Use it like this:
```xml
<appender name="infos2" class="com.gravitydev.appenders.KeyAndSecretAwsLogsAppender">
  <awsKey>AWSKEY</awsKey>
  <awsSecret>AWSSECRET</awsSecret>
  <logGroup>gravity-local</logGroup>
  <logStream>local</logStream>
  <layout class="ch.qos.logback.classic.PatternLayout">
    <pattern>%-5level %-30logger{40} %X{akkaSource} - %msg%n%xException{200}</pattern>
  </layout>   
</appender>
```

If you'd like to use a credentials provider, use this:

```xml
<appender name="infos2" class="com.gravitydev.appenders.CredentialsProviderAwsLogsAppender">
  <credentialsProvider class="com.amazonaws.auth.InstanceProfileCredentialsProvider"></credentialsProvider>
  <logGroup>gravity-local</logGroup>
  <logStream>local</logStream>
  <layout class="ch.qos.logback.classic.PatternLayout">
    <pattern>%-5level %-30logger{40} %X{akkaSource} - %msg%n%xException{200}</pattern>
  </layout>   
</appender>
```

