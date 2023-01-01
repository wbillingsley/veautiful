package docs.doctacular

import com.wbillingsley.veautiful.html._
import org.scalajs.dom

import docs.Common._

val introPage = <.div(
  markdown(
    s"""# Doctacular
      | 
      |Doctacular is a suite of components making it easy to build interactive materials. e.g.:
      |
      |  * Slide decks (in either a flat vertical notes-like view, or playable)
      |  * Tutorial-like challenges
      |  * Pages with a mix of markdown and interactive components
      |  * Teaching sites and open educational resources
      |
      |You can build a whole site with it, or it's also easy just to write and display individual slide deck.
      |
      |This documentation is built as a doctacular site. As are:
      |
      |  * [Thinking About Programming](https://theintelligentbook.com/thinkingaboutprogramming) - embedded robot simulations
      |    in a little course that teaches computational thinking.
      |  * [Circuits Up](https://theintelligentbook.com/circuitsup) - embedded circuit simulations in a little course
      |    that teaches computer architecture from electronics up.
      |  * [The Adventures of Will Scala](https://theintelligentbook.com/willscala) - a simpler site (mostly video
      |    and text) that goes alongside my undergraduate Scala course.
      |  * [Supercollaborative](https://theintelligentbook.com/supercollaborative) - for a software studio course, includes
      |    a git simulation built into the decks in the version control week.
      |
      |Writing a Doctacular site or deck should feel like scripting rather than programming.
      |
      |### Getting started with scala-cli
      |
      |As Doctacular "feels like" scripting, it works well with scala-cli
      |
      |```scala
      |import $$dep.`com.wbillingsley::doctacular::${docs.latestVersion}`
      |```
      |
      |
      |### Getting started with sbt or mill
      |
      |Doctacular is published to Maven Central.
      |
      |```
      |"com.wbillingsley" %%% "doctacular" % "${docs.latestVersion}"
      |```
      |""".stripMargin)
)