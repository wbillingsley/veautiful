package example

import com.wbillingsley.veautiful.html.{<, VHtmlNode, ^}
import com.wbillingsley.veautiful.templates.Challenge


object ChallengeExample {

  def page:VHtmlNode =  Common.layout(<.div(^.cls := "row",
    <.div(^.cls := "col", <.div())
  ))





  val challenge = new Challenge(Seq.empty, <.div("challenge!"), readyNext = false

  )

}
