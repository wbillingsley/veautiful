package example

import com.wbillingsley.veautiful.html.{<, VHtmlNode, ^}
import com.wbillingsley.veautiful.templates.Challenge
import com.wbillingsley.veautiful.templates.Challenge.{Level, VideoStage}


object ChallengeExample {



  val challenge = new Challenge(
    Seq(
      new Level("Intro", Seq(
        VideoStage("CircuitsVid"),
        VideoStage("Another video")
      ))
    )
  )

  def page(l:Int, s:Int):VHtmlNode =  Common.layout(<.div(^.cls := "row",
    <.div(^.cls := "col", <.div(^.cls := "resizable",
      challenge.show(l, s)
    ))
  ))





}
