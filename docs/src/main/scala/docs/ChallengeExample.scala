package docs

import com.wbillingsley.veautiful.doctacular.Challenge
import com.wbillingsley.veautiful.html.{<}
import org.scalajs.dom.{Element, Node}

object ChallengeExample {

  case class YouTubeStage(content:String) extends Challenge.Stage:
    val kind = "video"
    var rendered = false
    override def render = {
      rendered = true
      <.div(Challenge.textColumn(Common.markdown(content)))
    }
    override def completion: Challenge.Completion = if rendered then Challenge.Open else Challenge.Incomplete

  class MarkdownStage(content:String) extends Challenge.Stage:
    val kind = "text"
    var rendered = false
    override def render = {
      rendered = true
      <.div(Challenge.textColumn(Common.markdown(content)))
    }
    override def completion: Challenge.Completion = if rendered then Challenge.Open else Challenge.Incomplete

  val levels = Seq(
    Challenge.Level("Introduction", Seq(
      MarkdownStage(
        """## Challenges
          |
          |Challenges are a widget designed originally for hour-of-code style outreach experiences.
          |
          |The page displays like a slide deck, however there is a progress bar down the right hand side that is
          |divided into levels.
          |
          |Each level is is own deck - if you click next and previous between stateful stages within a level (e.g.
          |videos), the video should not reload. However, moving between levels the elements are removed from the
          |browser.
          |
          |""".stripMargin),
      MarkdownStage(
        """## Defining challenges
          |
          |Each level is made up of `Stage`s. This is a `DHtmlComponent` that is also required to implement two
          |extra methods:
          |
          |* `completion`, to report whether it is `Open`, `Incomplete`, or `Complete`
          |* `kind`, which is currently a string and largely used to choose which icon to use in the progress bar
          |
          |""".stripMargin
      )
    )),
    Challenge.Level("Level two", Seq(
      YouTubeStage("asdf"),
      MarkdownStage(
        """## Goodbye world
          |
          |This is just here so we can hop from one level to another in the example.
          |""".stripMargin)
    ))
  )

}
