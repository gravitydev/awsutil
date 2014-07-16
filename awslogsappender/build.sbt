
name := "awslogsappender"

organization := "com.gravitydev"

version := "0.0.1-SNAPSHOT"

crossScalaVersions := Seq("2.10.4", "2.11.1")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.6" % "test",
  "com.typesafe.akka" %% "akka-agent"  % "2.3.3",
  "com.gravitydev" %% "awsutil" % "0.0.1-SNAPSHOT",
  "ch.qos.logback" % "logback-core" % "1.1.2",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "org.slf4j" % "slf4j-api" % "1.7.7"
)

publishTo := Some("gravitydev" at "https://devstack.io/repo/gravitydev/public")

resolvers += "gravitydev" at "https://devstack.io/repo/gravitydev/public"

