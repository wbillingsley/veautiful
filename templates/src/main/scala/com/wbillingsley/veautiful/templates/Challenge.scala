package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.html.<.DElAppliable
import com.wbillingsley.veautiful.html.{<, DElement, VHtmlComponent, VHtmlNode, ^}

/**
  * Layout based on the one that is used for Escape the Lava Maze
  */
object Challenge {

  def stageHeader(stage:Int, name:String):VHtmlNode = {
    <.div(^.cls := "stage-header",
      <.div(^.cls := "media",
        <.img(^.cls := "stageninja", ^.src := "assets/ninja.png"),
        <.span(^.cls := "stagenumber", stage.toString),
        <.div(^.cls := "media-body",
          <.div(<.span(^.cls := "stagename", name))
        )
      )
    )
  }

  def hgutter = <.div(^.cls := "row hgutter")

  def card(s:String)(ac: DElAppliable *) = <.div(^.cls := "card",
    <.div(^.cls := "card-body",
      <.div(^.cls := "card-title", <.h4(s)),
      <.div(ac:_*)
    )
  )

  def card(ac: DElAppliable *) = <.div(^.cls := "card",
    <.div(^.cls := "card-body",
      <.div(ac:_*)
    )
  )

  def cardText(ac: DElAppliable *) = <.div(^.cls := "card-text", <.div(ac:_*))

  def textColumn(ac: DElAppliable *) = <.div(^.cls := "text-column", <.div(ac:_*))

  def split(l:DElement)(r:DElement) = <.div(^.cls := "split2",
    <.div(l),
    <.div(r)
  )

  def challengeLayout(header: => VHtmlNode = <.div(),
                      tr: => VHtmlNode = <.div(),
                      progressBlock: => VHtmlNode = <.div(),
                      pageControls: (Boolean) => VHtmlNode = _ => <.div(),
                      readyNext: => Boolean)(content:VHtmlNode) = {
    <.div(^.cls := "challenge-wrapper",
      <.div(^.cls := "challenge-header", header),
      <.div(^.cls := "challenge", content),
      <.div(^.cls := "countdown-box", tr),
      <.div(^.cls := "stage-progress", progressBlock),
      <.div(^.cls := "page-controls", pageControls(readyNext))
    )
  }

  trait Stage extends VHtmlComponent {

    def completion:Completion

    def kind:String

  }

  trait Level {

    def name:String

    def stages:Seq[Stage]

  }

  sealed trait Completion
  case object Open extends Completion
  case object Incomplete extends Completion
  case class Complete(mark:Option[Double], medal:Option[String]) extends Completion

}

class Challenge(levels: Seq[Challenge.Level],
                 header: => VHtmlNode = <.div(),
                 tr: => VHtmlNode = <.div(),
                 progressBlock: => VHtmlNode = <.div(),
                 pageControls: (Boolean) => VHtmlNode = _ => <.div(),
                 readyNext: => Boolean) {


  def show(level:Int, stage:Int):VHtmlNode = {

    levels(level).stages(stage)

  }

}