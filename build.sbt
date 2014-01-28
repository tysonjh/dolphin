name := "dolphin"

organization := "com.tysonjh"

version := "0.0.1"

scalaVersion := "2.10.3"

val paradiseVersion = "2.0.0-M3"

resolvers += Resolver.sonatypeRepo("snapshots")

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test" withSources() withJavadoc(),
  "org.scalacheck" %% "scalacheck" % "1.10.0" % "test" withSources() withJavadoc(),
  "org.scalamacros" % "quasiquotes" % paradiseVersion cross CrossVersion.full
)

libraryDependencies <+= (scalaVersion)("org.scala-lang" % "scala-reflect" % _ % "compile")

addCompilerPlugin("org.scalamacros" % "paradise" % paradiseVersion cross CrossVersion.full)

initialCommands := "import dolphin._"

