package example

import com.wbillingsley.veautiful.html.{<, VHtmlNode, ^}
import com.wbillingsley.veautiful.templates.Challenge


object ChallengeExample {



  val challenge = new Challenge(
    Seq.empty,
    <.div("challenge!"),
    readyNext = false
  )

  def page:VHtmlNode =  Common.layout(<.div(^.cls := "row",
    <.div(^.cls := "col", <.div(
      challenge

    ))
  ))





}
