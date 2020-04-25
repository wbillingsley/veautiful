// Turn this project into a Scala.js project by importing these settings

val versionStr = "0.1-SNAPSHOT"

val scalaVersionStr = "2.13.1"

lazy val veautiful = project.in(file("veautiful"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "veautiful",

    organization := "com.wbillingsley",

    version := versionStr,

    scalaVersion := scalaVersionStr,

    scalaJSUseMainModuleInitializer := false,

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "org.scalatest" %%% "scalatest" % "3.1.1" % "test"
    )
)

lazy val templates = project.in(file("templates"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful)
  .settings(
    name := "veautiful-templates",

    version := versionStr,

    scalaVersion := scalaVersionStr,

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "org.scalatest" %%% "scalatest" % "3.1.1" % "test"
    )
  )

lazy val scatter = project.in(file("scatter"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful)
  .settings(
    name := "scatter",

    version := versionStr,

    scalaVersion := scalaVersionStr,

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "org.scalatest" %%% "scalatest" % "3.1.1" % "test"
    )
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
  .settings(
    name := "wren",

    version := versionStr,

    scalaVersion := scalaVersionStr,

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "org.scalatest" %%% "scalatest" % "3.1.1" % "test"
    )
  )

val deployScript = taskKey[Unit]("Copies the fullOptJS script to deployscripts/")

lazy val docs = project.in(file("docs"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful, templates, scatter, wren)
  .settings(
    name := "veautiful-docs",

    version := versionStr,

    scalaVersion := scalaVersionStr,

    scalaJSUseMainModuleInitializer := true,

    scalacOptions ++= Seq("-unchecked", "-deprecation"),

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.0.0",
      "org.scalatest" %%% "scalatest" % "3.1.1" % "test"
    ),

    // Used by Travis-CI to get the script out from the .gitignored target directory
    // Don't run it locally, or you'll find the script gets loaded twice in index.html!
    deployScript := {
      val opt = (Compile / fullOptJS).value
      IO.copyFile(opt.data, new java.io.File("docs/deployscripts/compiled.js"))
    }
  )

