package example

import com.wbillingsley.veautiful.html.{<, VHtmlNode, ^}
import com.wbillingsley.veautiful.templates.Challenge
import com.wbillingsley.veautiful.templates.Challenge.{Level, VideoStage}


object ChallengeExample {



  val challenge = new Challenge(
    Seq(
      new Level("Intro", Seq(
        VideoStage("CircuitsVid")
      ))

    ),
    <.div("challenge!"),
    readyNext = false
  )

  def page:VHtmlNode =  Common.layout(<.div(^.cls := "row",
    <.div(^.cls := "col", <.div(^.cls := "resizable",
      challenge

    ))
  ))





}
