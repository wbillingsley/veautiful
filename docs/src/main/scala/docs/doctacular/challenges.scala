package docs.doctacular

import com.wbillingsley.veautiful.html._
import org.scalajs.dom

import docs.Common._

val challenges = <.div(
  markdown(
    """# Challenges
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
      |### Levels and Stages
      |
      |Challenges are divided into levels, that are then divided into stages. 
      |
      |At any time, one `Level` (including all its `Stages`) is loaded into the DOM, with only the current Stage made visible. This helps to ensure that
      |clicking `Next` and `Back` doesn't causes too many video or resource re-loads.
      |
      |A level also has a name, while a stage only has an icon.
      |
      |
      |
      |
      |""".stripMargin)
)