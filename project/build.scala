import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object build extends Build
{
    val Organization = "com.github.razvanpanda"
    val Name = "RealityQuest"
    val Version = "0.1.0-SNAPSHOT"
    val ScalaVersion = "2.10.3"

    lazy val project = Project(
        Name,
        file("."),
        settings = Defaults.defaultSettings ++ assemblySettings ++ Seq(
            organization := Organization,
            name := Name,
            version := Version,
            scalaVersion := ScalaVersion,
            jarName in assembly := "RealityQuest.jar",
            unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar")),
            resolvers ++= Seq(
                "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                "Sonatype snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots/"
            ),
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
                "net.java.dev.jna"       %  "platform"          % "3.5.2",
                "org.scalafx"            %  "scalafx_2.10"      % "1.0.0-M6"
            )
        )
    )
}