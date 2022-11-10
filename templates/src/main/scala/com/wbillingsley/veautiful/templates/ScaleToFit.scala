package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.MakeItSo
import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlNode, ^}
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

case class ScaleToFit(width:Int, height:Int)(var content: VHtmlNode) extends VHtmlComponent with MakeItSo {

  var scale:Double = 1
  var top:Double = 0
  var left:Double = 0

  def rescale() = for { n <- domNode } {
    val r = n.asInstanceOf[HTMLElement].getBoundingClientRect()
    scale = Math.min(r.height / height, r.width / width)

    left = Math.max((r.width - scale * width) / 2, 0)
    top = Math.max((r.height - scale * height) / 2, 0)

    println(s"Scale is now $scale")
  }

  val rescaleEventListener:Event => Unit = { e => 
    rerender()
  }

  override def afterAttach(): Unit = {
    super.afterAttach()
    dom.window.addEventListener("resize", rescaleEventListener)
  }

  override def beforeDetach(): Unit = {
    super.beforeDetach()
    dom.window.removeEventListener("resize", rescaleEventListener)
  }

  def render = {
    <.div(^.cls := "vslides-top",
      <.div(^.cls := "vslides-scaler", ^.attr("style") := s"transform: scale($scale); width: ${width}px; height: ${height}px; top: ${top}px; left: ${left}px; ",
        content
      )
    )
  }

  def makeItSo = {
    case s:ScaleToFit =>
      this.content = s.content
      rerender()
  }

}