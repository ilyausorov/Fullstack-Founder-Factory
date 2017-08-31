name := """Fullstack-Founder-Factory"""

version := "5.2-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala).enablePlugins(SbtTwirl)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  filters,
  specs2 % Test,
  "com.github.nscala-time" %% "nscala-time" % "2.0.0",  
  "org.flywaydb" %% "flyway-play" % "2.0.1",
  "com.nulab-inc" %% "play2-oauth2-provider" % "0.15.1",
  "com.github.tototoshi" %% "slick-joda-mapper" % "2.0.0",
  "com.github.tototoshi" %% "scala-csv" % "1.3.4",  
  "io.github.cloudify" %% "spdf" % "1.3.3",
  "com.typesafe" % "config" % "1.3.0",
  "commons-io" % "commons-io" % "2.3",
  "com.google.zxing" % "zxing-parent" % "3.2.1",
  "com.google.zxing" % "core" % "3.2.1",
  "com.google.zxing" % "javase" % "3.2.1",
  "javax.mail" % "mail" % "1.5.0-b01"
  ,"com.typesafe.akka" %% "akka-actor" % "2.3.11"
  ,"mysql" % "mysql-connector-java" % "5.1.25"
  ,"org.mindrot" % "jbcrypt" % "0.3m"
  ,"com.typesafe.play" %% "play-slick" % "1.1.1"
  ,"org.joda" % "joda-convert" % "1.7"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

routesGenerator := InjectedRoutesGenerator