resolvers += "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea"     % "1.6.0")

addSbtPlugin("org.scalatra.sbt"     % "scalatra-sbt" % "0.3.5")

addSbtPlugin("com.eed3si9n"         % "sbt-assembly" % "0.11.2")