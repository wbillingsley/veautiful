package docs

import com.wbillingsley.veautiful.DiffNode
import com.wbillingsley.veautiful.templates.Challenge
import com.wbillingsley.veautiful.html.{<}
import org.scalajs.dom.{Element, Node}

object ChallengeExample {

  class MarkdownStage(content:String) extends Challenge.Stage:
    val kind = "text"
    var rendered = false
    override def render: DiffNode[Element, Node] = {
      rendered = true
      <.div(Common.markdown(content))
    }
    override def completion: Challenge.Completion = if rendered then Challenge.Open else Challenge.Incomplete

  val levels = Seq(
    Challenge.Level("One", Seq(
      MarkdownStage("## Hello world")
    )),
    Challenge.Level("One", Seq(
      MarkdownStage("## Goodbye world")
    ))
  )

}
