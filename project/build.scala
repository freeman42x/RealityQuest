import org.scalatra.sbt._
import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._
import org.sbtidea.SbtIdeaPlugin._

object build extends Build
{
    val Organization = "com.github.razvanpanda"
    val Name = "RealityQuest"
    val Version = "0.1.0-SNAPSHOT"
    val AkkaVersion = "2.2.3"
    val ScalaVersion = "2.10.3"
    val ScalatraVersion = "2.3.0.RC1"
    val JettyVersion = "9.1.3.v20140225"

    lazy val project = Project(
        Name,
        file("."),
        settings = Defaults.defaultSettings ++ ScalatraPlugin.scalatraSettings ++ assemblySettings ++ Seq(
            organization := Organization,
            name := Name,
            version := Version,
            scalaVersion := ScalaVersion,
            unmanagedResourceDirectories in Compile += baseDirectory.value / "src/main/resources_web",
            jarName in assembly := "RealityQuest.jar",
            mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
                {
                    case "about.html" => MergeStrategy.first
                    case x => old(x)
                }
            },
            unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/jre/lib/jfxrt.jar")),
            scalacOptions ++= Seq("-feature"),
            ideaExcludeFolders := ".idea" :: ".idea_modules" :: "bower_components" :: "db" :: Nil,
            resolvers ++= Seq(
                "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                "Sonatype snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots/"
            ),
            libraryDependencies ++= Seq(
                "org.scalatest"               %% "scalatest"                 % "2.1.0-RC2",
                "com.typesafe.akka"           %% "akka-actor"                % AkkaVersion,
                "com.typesafe.akka"           %% "akka-testkit"              % AkkaVersion,
                "com.typesafe.akka"           %% "akka-slf4j"                % AkkaVersion,
                "ch.qos.logback"              %  "logback-classic"           % "1.1.1",
                "com.typesafe.slick"          %% "slick"                     % "2.0.0",
                "com.h2database"              %  "h2"                        % "1.3.174",
                "joda-time"                   %  "joda-time"                 % "2.3",
                "com.github.nscala-time"      %% "nscala-time"               % "0.6.0",
                "com.github.tototoshi"        %% "slick-joda-mapper"         % "1.0.0",
                "net.java.dev.jna"            %  "jna"                       % "4.0.0",
                "net.java.dev.jna"            %  "platform"                  % "3.5.2",
                "org.scalafx"                 %% "scalafx"                   % "1.0.0-M6",
                "org.scalatra"                %% "scalatra"                  % ScalatraVersion,
                "org.scalatra"                %% "scalatra-specs2"           % ScalatraVersion  % "test",
                "org.scalatra"                %% "scalatra-json"             % ScalatraVersion,
                "org.scalatra"                %% "scalatra-atmosphere"       % ScalatraVersion,
                "org.json4s"                  %% "json4s-jackson"            % "3.2.7",
                "org.json4s"                  %% "json4s-ext"                % "3.1.0",
                "org.eclipse.jetty"           %  "jetty-webapp"              % JettyVersion     % "compile",
                "org.eclipse.jetty"           %  "jetty-plus"                % JettyVersion     % "compile;container",
                "org.eclipse.jetty.websocket" %  "websocket-server"          % JettyVersion     % "compile;container",
                "javax.servlet"               %  "javax.servlet-api"         % "3.1.0"          % "compile;container"
            )
        )
    )
}