import sbt.Keys.testFrameworks
// Turn this project into a Scala.js project by importing these settings

val versionStr = "0.1-SNAPSHOT"

val scalaVersionStr = "3.0.0"

lazy val commonSettings = Seq(
  version := versionStr,

  organization := "com.wbillingsley",

  scalaVersion := scalaVersionStr,

  libraryDependencies ++= Seq(
    ("org.scala-js" %%% "scalajs-dom" % "1.1.0").cross(CrossVersion.for3Use2_13),
    "org.scalameta" %%% "munit" % "0.7.26" % Test
  ),

  testFrameworks += new TestFramework("munit.Framework"),

  Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }
)

lazy val veautiful = project.in(file("veautiful"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings:_*)
  .settings(
    name := "veautiful",
    scalaJSUseMainModuleInitializer := false,

    // Temporarily disable doc generation until https://github.com/sbt/sbt/pull/6499 is merged into sbt
    Compile / doc / sources := Seq()
  )

lazy val templates = project.in(file("templates"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful)
  .settings(commonSettings:_*)
  .settings(
    name := "veautiful-templates",
  )

lazy val scatter = project.in(file("scatter"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful)
  .settings(commonSettings:_*)
  .settings(
    name := "scatter",

    // Temporarily disable doc generation until https://github.com/sbt/sbt/pull/6499 is merged into sbt
    Compile / doc / sources := Seq()
  )

/**
  * Circuit constraint propagator - based on the intelligent book one.
  * "This little structure (excellent *Sir Kit*)
  *  Holds forth to us that you bestow'd more wit
  *  In building it than on all Paul's beside" - from a broadsheet on Sir Christopher Wren and his "Happy invention of
  *  a pulpit on wheels"
  */
lazy val wren = project.in(file("wren"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful)
  .settings(commonSettings:_*)
  .settings(
    name := "wren",
  )

/**
  * Doctacular is a set of routes and templates to make it easy to set up documentation sites
  */
lazy val doctacular = project.in(file("doctacular"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful, templates)
  .settings(commonSettings:_*)
  .settings(
    name := "doctacular",
  )


val deployScript = taskKey[Unit]("Copies the fullOptJS script to deployscripts/")

lazy val docs = project.in(file("docs"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful, templates, scatter, wren, doctacular)
  .settings(commonSettings:_*)
  .settings(
    name := "veautiful-docs",
    scalaJSUseMainModuleInitializer := true,
    scalacOptions ++= Seq("-unchecked", "-deprecation"),
    // Used by Travis-CI to get the script out from the .gitignored target directory
    // Don't run it locally, or you'll find the script gets loaded twice in index.html!
    deployScript := {
      val opt = (Compile / fullOptJS).value
      IO.copyFile(opt.data, new java.io.File("docs/deployscripts/compiled.js"))
    }
  )

