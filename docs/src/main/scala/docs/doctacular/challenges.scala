package docs.doctacular

import com.wbillingsley.veautiful.html._
import org.scalajs.dom

import docs.Common._

val challenges = <.div(
  markdown(
    s"""# Challenges
      |
      |`Challenges` are full-screen landscape tutorials that are divided into levels and stages.
      |
      |To understand them, perhaps first take a look at a couple of examples:
      |
      |* [Turtle Graphics from Thinking About Programming](https://theintelligentbook.com/thinkingaboutprogramming/#/challenges/turtleGraphics/0/0)
      |* [Git locally from Supercollaborative](https://theintelligentbook.com/supercollaborative/#/challenges/gitLocalTutorial/0/0)
      |
      |Like slide decks, they scale themselves to the screen so that you can lay out your tutorial as if you were working with a fixed resolution.
      |
      |### Levels
      |
      |Challenges are divided into levels, that are then divided into stages. This is the internal definition of the Level class:
      |
      |```scala
      |/** A Level has a name and a sequences of Stages */
      |case class Level(name:String, stages:Seq[Stage])
      |```
      |
      |As you can see, a Level is simply a named sequence of stages.
      |
      |### Stages
      |
      |Stages implement the `Stage` trait. These are `DHtmlComponent`s (i.e. [stateful components](${docs.site.router.path(docs.site.PageRoute("stateful-components"))})) that also publish two additional methods:
      |
      |* `kind:String`, which changes the icon that is shown for the stage
      |* `completion:Completion`, which indicates how far through the stage the user has progressed. e.g. if they've completed it
      |
      |Typically, a site will declare some set of stage classes. e.g.
      |
      |```scala
      |  class MarkdownStage(content:String) extends Challenge.Stage:
      |    val kind = "text"
      |    var rendered = false
      |    override def render = {
      |      rendered = true
      |      <.div(marked.div(content))
      |    }
      |    override def completion: Challenge.Completion = if rendered then Challenge.Open else Challenge.Incomplete
      |```
      |
      |Later, I'll provide a set of these, but at the moment, sites are still settling on what seems to work best in terms of layout.
      |
      |A challenge can then be built as a sequence of levels:
      |
      |```scala
      |def myChallenge = Seq(
      |  Level("Intro", Seq(
      |    YouTubeStage(someVideo),
      |    MarkdownStage(someContent),
      |    ExerciseStage(someExercise)
      |  )),
      |  Level("Summary", Seq(
      |    MarkdownStage(someContent),
      |    ExerciseStage(someExercise)
      |  ))
      |)
      |```
      |
      |They can then be added to a site
      |
      |```scala
      |import site.given
      |
      |site.toc = site.Toc(
      |  // Preceeding routes
      |
      |  "My challenge" -> site.addChallenge("my-challenge", myChallenge)
      |)
      |```
      |
      |### What shows in the DOM
      |
      |At any time, one `Level` (including all its `Stages`) is loaded into the DOM, with only the current Stage made visible. This helps to ensure that
      |clicking `Next` and `Back` doesn't causes too many video or resource re-loads.
      |
      |""".stripMargin),
)