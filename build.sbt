name := "RealityQuestAgent"
 
version := "1.0"
 
scalaVersion := "2.10.2"
 
resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.2.3"

libraryDependencies += "com.typesafe.slick" % "slick_2.10" % "2.0.0-M3"

libraryDependencies += "com.github.tototoshi" %% "slick-joda-mapper" % "1.0.0-SNAPSHOT"

libraryDependencies += "com.h2database" % "h2" % "1.3.174"

libraryDependencies += "joda-time" % "joda-time" % "2.3"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.5"

libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.5"