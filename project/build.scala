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
                "org.scalatest"           %  "scalatest_2.10"        % "2.1.0-RC2",
                "com.typesafe.akka"       %% "akka-actor"            % "2.2.3",
                "com.typesafe.akka"       %  "akka-testkit_2.10"     % "2.3.0-RC4",
                "com.typesafe.akka"       %  "akka-slf4j_2.10"       % "2.3.0-RC2",
                "ch.qos.logback"          %  "logback-classic"       % "1.1.1",
                "com.typesafe.slick"      %  "slick_2.10"            % "2.0.0",
                "com.h2database"          %  "h2"                    % "1.3.174",
                "joda-time"               %  "joda-time"             % "2.3",
                "com.github.nscala-time"  %% "nscala-time"           % "0.6.0",
                "com.github.tototoshi"    % "slick-joda-mapper_2.10" % "1.0.0",
                "net.java.dev.jna"        %  "jna"                   % "4.0.0",
                "net.java.dev.jna"        %  "platform"              % "3.5.2",
                "org.scalafx"             %  "scalafx_2.10"          % "1.0.0-M6",
                "org.scalatra"            %% "scalatra"              % ScalatraVersion,
                "org.scalatra"            %% "scalatra-specs2"       % ScalatraVersion % "test",
                "org.scalatra"            %% "scalatra-json"         % "2.2.2",
                "org.json4s"              %% "json4s-jackson"        % "3.2.6",
                "org.json4s"              %  "json4s-ext_2.10"       % "3.1.0",
                "org.eclipse.jetty"       %  "jetty-webapp"          % "9.1.1.v20140108" % "container;compile",
                "org.eclipse.jetty.orbit" %  "javax.servlet"         % "3.0.0.v201112011016" % "container;provided;test" artifacts Artifact("javax.servlet", "jar", "jar")
            )
        )
    )
}