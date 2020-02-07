package example

import com.wbillingsley.veautiful.html.{<, VHtmlNode, ^}
import com.wbillingsley.veautiful.templates.Challenge
import com.wbillingsley.veautiful.templates.Challenge.{Level, VideoStage}


object ChallengeExample {



  val challenge = Challenge(
    Seq(
      new Level("Intro", Seq(
        VideoStage("CircuitsVid"),
        VideoStage("Another video")
      ))
    ),
    homePath = { _ => Router.path(IntroRoute) },
    levelPath = { (c, l) => Router.path(ChallengeRoute(l, 0)) },
    stagePath = { (c, l, s) => Router.path(ChallengeRoute(l, s)) },
    scaleToWindow = false
  )

  def page(l:Int, s:Int):VHtmlNode =
      challenge.show(l, s)





}
