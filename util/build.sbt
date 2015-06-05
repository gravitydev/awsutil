
name := "awsutil"

organization := "com.gravitydev"

version := "0.0.4-SNAPSHOT"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.9.39",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

publishTo := Some("gravitydev" at "https://devstack.io/repo/gravitydev/public")
