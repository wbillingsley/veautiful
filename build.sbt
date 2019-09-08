// Turn this project into a Scala.js project by importing these settings

val versionStr = "0.1-SNAPSHOT"

val scalaVersionStr = "2.12.8"

lazy val veautiful = project.in(file("veautiful"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "veautiful",

    organization := "com.wbillingsley",

    version := versionStr,

    scalaVersion := scalaVersionStr,

    scalaJSUseMainModuleInitializer := false,

    testFrameworks += new TestFramework("utest.runner.Framework"),

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.2",
      "com.lihaoyi" %%% "utest" % "0.4.5" % "test",
      "org.scalatest" %%% "scalatest" % "3.0.0" % "test"
    )
)

lazy val templates = project.in(file("templates"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful)
  .settings(
    name := "veautiful-templates",

    version := versionStr,

    scalaVersion := scalaVersionStr,

    scalaJSUseMainModuleInitializer := true,

    testFrameworks += new TestFramework("utest.runner.Framework"),

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.2",
      "com.lihaoyi" %%% "utest" % "0.4.5" % "test",
      "org.scalatest" %%% "scalatest" % "3.0.0" % "test"
    )
  )

lazy val scatter = project.in(file("scatter"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful)
  .settings(
    name := "scatter",

    version := versionStr,

    scalaVersion := scalaVersionStr,

    scalaJSUseMainModuleInitializer := true,

    testFrameworks += new TestFramework("utest.runner.Framework"),

    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.2",
      "com.lihaoyi" %%% "utest" % "0.4.5" % "test",
      "org.scalatest" %%% "scalatest" % "3.0.0" % "test"
    )
  )


lazy val docs = project.in(file("docs"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful, templates)
  .settings(
      name := "veautiful-docs",

      version := versionStr,

      scalaVersion := scalaVersionStr,

      scalaJSUseMainModuleInitializer := true,

      testFrameworks += new TestFramework("utest.runner.Framework"),

      libraryDependencies ++= Seq(
        "org.scala-js" %%% "scalajs-dom" % "0.9.2",
        "com.lihaoyi" %%% "utest" % "0.4.5" % "test",
        "org.scalatest" %%% "scalatest" % "3.0.0" % "test"
      )
  )


