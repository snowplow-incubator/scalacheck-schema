lazy val root = project.in(file("."))
  .settings(
    name := "scalacheck-schema",
    version := "0.1.0-M3",
    organization := "com.snowplowanalytics",
    description := "ScalaCheck generators for various Iglu-compatible Schema formats",
    scalaVersion := "2.11.12",
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0"))
  )
  .settings(BuildSettings.buildSettings)
  .settings(BuildSettings.publishSettings)
  .settings(
    resolvers ++= Seq(
      "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"
    ),
    libraryDependencies ++= Seq(
      Dependencies.igluClient,
      Dependencies.schemaDdl,
      Dependencies.scalaCheck,
      Dependencies.scalaCheckCats,
      Dependencies.validator,
      Dependencies.specs2,
      Dependencies.specs2ScalaCheck
    )
  )
  .settings(BuildSettings.helpersSettings)

