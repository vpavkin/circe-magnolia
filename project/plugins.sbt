resolvers ++= Seq(
  Classpaths.typesafeReleases,
  Classpaths.sbtPluginReleases,
  "jgit-repo" at "http://download.eclipse.org/jgit/maven"
)

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.5.1")
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.4.2")
addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.11")
addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.2")
addSbtPlugin("com.typesafe.sbt" % "sbt-ghpages" % "0.6.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-git" % "1.0.0")
addSbtPlugin("com.typesafe.sbt" % "sbt-site" % "1.3.2")
addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "0.6.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.26")
addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.13.1")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.4.0")
addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.3")
