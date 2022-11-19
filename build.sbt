ThisBuild / version := "0.1.0-SNAPSHOT"

val catsVersion = "2.9.0"

ThisBuild / scalaVersion := "2.12.11"

lazy val root = (project in file("."))
  .settings(
    organization := "org.gsoldevila",
    name := "scalacollections",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion,
      "org.typelevel" %% "cats-free" % catsVersion,
      "org.typelevel" %% "cats-laws" % catsVersion,
      "com.storm-enroute" %% "scalameter-core" % "0.20"
    ),
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-language:_"
    )
  )
