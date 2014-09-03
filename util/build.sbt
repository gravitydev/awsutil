
name := "awsutil"

organization := "com.gravitydev"

version := "0.0.2-SNAPSHOT"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.8.4",
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)

publishTo := Some("gravitydev" at "https://devstack.io/repo/gravitydev/public")
