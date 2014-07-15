
name := "awsutil"

organization := "com.gravitydev"

version := "0.0.1-SNAPSHOT"

crossScalaVersions := Seq("2.10.4", "2.11.1")

libraryDependencies ++= Seq(
  "com.amazonaws"             % "aws-java-sdk" % "1.8.4"
)

publishTo := Some("gravitydev" at "https://devstack.io/repo/gravitydev/public")

