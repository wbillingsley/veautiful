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

  def textAndEx(left: DElAppliable *)(right: DElAppliable *):VHtmlNode = {
    split(textColumn(left:_*))(right:_*)
  }

  def split(l:DElAppliable*)(r:DElAppliable*) = <.div(^.cls := "split2",
    <.div(l:_*),
    <.div(r:_*)
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

  case class VideoStage(yt:String) extends Stage {
    var completion = Open

    val kind = "video"

    def render = <.div("This will be a video")
  }

  case class Level(name:String, stages:Seq[Stage])

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
                 readyNext: => Boolean) extends VHtmlComponent {

  var level:Int = 0
  var stage:Int = 0

  def show(l:Int, s:Int):VHtmlNode = {
    level = l
    stage = s
    rerender()
  }

  def elements = {
    for {
      l <- levels
      s <- l.stages
    } yield s
  }

  def layout(s:Sequencer, si:SequenceItem, i:Int):VHtmlNode = {
    Challenge.challengeLayout(
      <.div("This is my header"),
      <.div("Top right"),
      <.div("progress block"),
      (_) => <.div("Page controls"),
      true
    )(si.content)
  }

  def render = {
    /*<.div(^.attr("id") := "challenge-element",
      ScaleToFit(1920, 1080)(levels(level).stages(stage))
    )*/
    <.div(
      VSlides(1920, 1080, layout=layout)(elements)
    )
  }

}