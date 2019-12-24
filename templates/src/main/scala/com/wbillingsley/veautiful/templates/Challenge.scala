package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.<.DElAppliable
import com.wbillingsley.veautiful.html.VHtmlNode
import com.wbillingsley.veautiful.{<, DElement, ^}
import org.scalajs.dom.raw.HTMLTextAreaElement

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

}
