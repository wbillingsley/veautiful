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
    homeIcon = <.span(^.cls := "material-icons", <("i")("home")),
    levelPath = { (c, l) => Router.path(ChallengeRoute(l, 0)) },
    stagePath = { (c, l, s) => Router.path(ChallengeRoute(l, s)) }
  )

  def page(l:Int, s:Int):VHtmlNode =  Common.layout(<.div(^.cls := "row",
    <.div(^.cls := "col", <.div(^.cls := "resizable",
      challenge.show(l, s)
    ))
  ))





}
