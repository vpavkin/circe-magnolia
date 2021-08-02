import ReleaseTransformations._
import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

lazy val buildSettings = Seq(
  organization := "io.circe",
  scalaVersion := "3.0.0"
)

lazy val magnoliaVersion = "0.0.1-M4"
lazy val circeVersion = "0.15.0-M1"
lazy val circeGenericExtrasVersion = "0.13.0"
lazy val shapelessVersion = "2.3.3"
lazy val scalatestVersion = "3.2.2"
lazy val scalacheckVersion = "1.14.3"

lazy val compilerSettings = Seq(
  scalacOptions ++= Seq(
    //    "-deprecation",
    //    "-encoding", "utf-8",
    //    "-explaintypes",
    //    "-feature",
    //    "-language:existentials",
    //    "-language:experimental.macros",
    //    "-language:higherKinds",
    //    "-language:implicitConversions",
    //    "-unchecked",
    //    "-Xcheckinit",
    //    "-Xfatal-warnings",
    //    "-Xlint:adapted-args",
    //    "-Xlint:delayedinit-select",
    //    "-Xlint:doc-detached",
    //    "-Xlint:inaccessible",
    //    "-Xlint:infer-any",
    //    "-Xlint:missing-interpolator",
    //    "-Xlint:nullary-override",
    //    "-Xlint:nullary-unit",
    //    "-Xlint:option-implicit",
    //    "-Xlint:package-object-classes",
    //    "-Xlint:poly-implicit-overload",
    //    "-Xlint:private-shadow",
    //    "-Xlint:stars-align",
    //    "-Xlint:type-parameter-shadow",
    //    "-Ywarn-dead-code",
    //    "-Ywarn-numeric-widen",
    //    "-Ywarn-value-discard",
    //    "-Xlint:constant",
    //    "-Ywarn-macros:after",
    //    "-Ywarn-extra-implicit",
    //    "-Ywarn-unused:implicits",
    //    "-Ywarn-unused:imports",
    //    "-Ywarn-unused:locals",
    //    "-Ywarn-unused:params",
    //    "-Ywarn-unused:patvars",
    //    "-Ywarn-unused:privates"
    "-indent",
    "-rewrite",
    "-explain-types",
    "-encoding",
    "UTF-8"
  )
)

lazy val allSettings = buildSettings ++ compilerSettings ++ publishSettings

lazy val coreDependencies = libraryDependencies ++= Seq(
  "io.circe" %%% "circe-core" % circeVersion,
  "com.softwaremill.magnolia1_3" %% "magnolia" % "1.0.0-M4"
)

lazy val testDependencies = libraryDependencies ++= Seq(
//  "com.chuusai" %% "shapeless" % shapelessVersion,
//  "io.circe" %%% "circe-parser" % circeVersion,
//  "io.circe" %%% "circe-generic" % circeVersion,
//  "io.circe" %%% "circe-generic-extras" % circeGenericExtrasVersion,
//  "io.circe" %%% "circe-testing" % circeVersion,
//  "org.scalacheck" %%% "scalacheck" % scalacheckVersion,
//  "org.scalatest" %%% "scalatest" % scalatestVersion
)

lazy val circeMagnolia = project
  .in(file("."))
  .settings(name := "circe-magnolia")
  .settings(allSettings: _*)
  .settings(noPublishSettings: _*)
  .aggregate(derivation)
  .dependsOn(derivation)

lazy val derivation = project
  .in(file("derivation"))
  .settings(
    description := "Magnolia-based derivation for Circe codecs",
    moduleName := "circe-magnolia-derivation",
    name := "derivation"
  )
  .settings(allSettings: _*)
  .settings(coreDependencies)

lazy val tests = project
  .in(file("tests"))
  .settings(
    description := "Circe-magnolia tests",
    moduleName := "circe-magnolia-tests",
    name := "tests"
  )
  .settings(allSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(coreDependencies)
  .settings(testDependencies)
  .settings(
    // Coverage disabled due to https://github.com/scoverage/scalac-scoverage-plugin/issues/269
    // coverageExcludedPackages :=".*"
  )
  .dependsOn(derivation)

lazy val noPublishSettings = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false
)

lazy val publishSettings = Seq(
  releaseIgnoreUntrackedFiles := true,
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  homepage := Some(url("https://github.com/circe/circe-magnolia")),
  licenses := Seq(
    "Apache 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")
  ),
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots".at(nexus + "content/repositories/snapshots"))
    else
      Some("releases".at(nexus + "service/local/staging/deploy/maven2"))
  },
  autoAPIMappings := true,
  apiURL := Some(url("https://vpavkin.github.io/circe-magnolia/api/")),
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/circe/circe-magnolia"),
      "scm:git:git@github.com:circe/circe-magnolia.git"
    )
  ),
  pomExtra :=
    <developers>
      <developer>
        <id>vpavkin</id>
        <name>Vladimir Pavkin</name>
        <url>http://pavkin.ru</url>
      </developer>
    </developers>
)

lazy val sharedReleaseProcess = Seq(
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    runTest,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommandAndRemaining(command = "+publishSigned"),
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = releaseStepCommand("sonatypeReleaseAll")),
    pushChanges
  )
)

addCommandAlias("validate", ";compile;testsJVM/test;testsJS/test")
