import org.scalatra.sbt._
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
    val ScalatraVersion = "2.2.2"

    lazy val project = Project(
        Name,
        file("."),
        settings = Defaults.defaultSettings ++ ScalatraPlugin.scalatraWithJRebel ++ assemblySettings ++ Seq(
            organization := Organization,
            name := Name,
            version := Version,
            scalaVersion := ScalaVersion,
            unmanagedResourceDirectories in Compile += baseDirectory.value / "src/main/resources_web",
            jarName in assembly := "RealityQuest.jar",
            mergeStrategy in assembly <<= (mergeStrategy in assembly)
            {
                (old) =>
                {
                    case "about.html" => MergeStrategy.first
                    case x => old(x)
                }
            },
            unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar")),
            resolvers ++= Seq(
                "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                "Sonatype snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots/"
            ),
            libraryDependencies ++= Seq(
                "com.typesafe.akka"       %% "akka-actor"        % "2.2.3",
                "com.typesafe.slick"      %  "slick_2.10"        % "2.0.0-M2",
                "org.slf4j"               %  "slf4j-api"         % "1.7.5",
                "org.slf4j"               %  "slf4j-nop"         % "1.7.5",
                "com.h2database"          %  "h2"                % "1.3.174",
                "joda-time"               %  "joda-time"         % "2.3",
                "com.github.nscala-time"  %% "nscala-time"       % "0.6.0",
                "com.github.tototoshi"    %% "slick-joda-mapper" % "1.0.0-SNAPSHOT",
                "net.java.dev.jna"        %  "jna"               % "4.0.0",
                "net.java.dev.jna"        %  "platform"          % "3.5.2",
                "org.scalafx"             %  "scalafx_2.10"      % "1.0.0-M6",
                "org.scalatra"            %% "scalatra"          % ScalatraVersion,
                "org.scalatra"            %% "scalatra-specs2"   % ScalatraVersion % "test",
                "org.eclipse.jetty"       %  "jetty-webapp"      % "8.1.8.v20121106" % "container;compile",
                "org.eclipse.jetty.orbit" %  "javax.servlet"     % "3.0.0.v201112011016" % "container;provided;test" artifacts Artifact("javax.servlet", "jar", "jar")
            )
        )
    )
}