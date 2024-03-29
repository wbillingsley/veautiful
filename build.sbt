import sbt.Keys.testFrameworks
import scala.sys.process._

val versionStr = "0.1-SNAPSHOT"

val scalaVersionStr = "3.1.2"

inThisBuild(List(
  organization := "com.wbillingsley",
  homepage := Some(url("https://www.wbillingsley.com/veautiful")),
  licenses := List(License.MIT),
  developers := List(
    Developer(
      "wbillingsley",
      "William Billingsley",
      "wbillingsley@cantab.net",
      url("https://www.wbillingsley.com")
    )
  )
))

lazy val commonSettings = Seq(
  scalaVersion := scalaVersionStr,

  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "2.3.0",
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

    // Don't publish to Maven Central yet, as much of this is in Circuits Up instead at the moment
    publish / skip := true,
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


val deployFast = taskKey[Unit]("Copies the fastLinkJS script to compiled.js")
val deployFull = taskKey[Unit]("Copies the fullLinkJS script to compiled.js")

lazy val docs = project.in(file("docs"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(veautiful, templates, scatter, wren, doctacular)
  .settings(commonSettings:_*)
  .settings(
    name := "veautiful-docs",

    // Don't publish the documentation site to Maven Central
    publish / skip := true,
    
    scalaJSUseMainModuleInitializer := true,

    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }, // At the moment in Scala.js, ESModule would cause Closure minimisation to get turned off.

    scalacOptions ++= Seq("-unchecked", "-deprecation"),

    deployFast := {
      val opt = (Compile / fastLinkJS).value
      (
        Process(s"npx webpack --config webpack.config.js --env entry=./target/scala-3.1.2/veautiful-docs-fastopt/main.js --env mode=development", Some(new java.io.File("docs")))
      ).!
    },

    deployFull := {
      val opt = (Compile / fullLinkJS).value
      (
        Process(s"npx webpack --config webpack.config.js --env entry=./target/scala-3.1.2/veautiful-docs-opt/main.js --env mode=production", Some(new java.io.File("docs")))
      ).!
    }
  )

// Don't publish the root/aggregate project
publish / skip := true

//Uncomment this block to stop sbt loading but print out the dynamically generated version
//Global / onLoad := (Global / onLoad).value.andThen { s =>
//  dynverAssertTagVersion.value
//  s
//}

