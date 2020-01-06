package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^}
import com.wbillingsley.veautiful.{MakeItSo}
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

case class VSlides(width: Int, height: Int, override val key: Option[String] = None, layout:Sequencer.LayoutFunc = Sequencer.defaultLayout)(var content:Seq[SequenceItem], var index:Int = 0) extends VHtmlComponent with MakeItSo {

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

  def rescaleEventListener(e:Event):Unit = {
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

  def slide(n:VHtmlNode) = <.div(^.cls := "vslide",
    n
  )


  override def render: VHtmlDiffNode = {
    rescale()

    <.div(^.cls := "vslides-top",
      <.div(^.cls := "vslides-scaler", ^.attr("style") := s"transform: scale($scale); width: ${width}px; height: ${height}px; top: ${top}px; left: ${left}px; ",
        Sequencer(layout=layout)(
          content, index
        )
      )
    )
  }

  override def makeItSo: PartialFunction[MakeItSo, _] = {
    case v:VSlides =>
      content = v.content
      index = v.index
      rerender()
  }
}
