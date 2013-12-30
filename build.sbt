import sbtassembly.Plugin._
import AssemblyKeys._

assemblySettings

jarName in assembly := "RealityQuestAgent.jar"

name := "RealityQuestAgent"
 
version := "1.0"
 
scalaVersion := "2.10.2"

resolvers ++= Seq(
    "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
    "Sonatype snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots/"
)

libraryDependencies ++= Seq(
    "com.typesafe.akka"      %% "akka-actor"        % "2.2.3",
    "com.typesafe.slick"     %  "slick_2.10"        % "2.0.0-M2",
    "org.slf4j"              %  "slf4j-api"         % "1.7.5",
    "org.slf4j"              %  "slf4j-nop"         % "1.7.5",
    "com.h2database"         %  "h2"                % "1.3.174",
    "joda-time"              %  "joda-time"         % "2.3",
    "com.github.nscala-time" %% "nscala-time"       % "0.6.0",
    "com.github.tototoshi"   %% "slick-joda-mapper" % "1.0.0-SNAPSHOT",
    "net.java.dev.jna"       %  "jna"               % "4.0.0",
    "net.java.dev.jna"       %  "platform"          % "3.5.2"
)