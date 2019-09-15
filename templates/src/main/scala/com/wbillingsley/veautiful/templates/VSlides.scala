package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.{<, DiffComponent, DiffNode, VNode, ^}
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

case class VSlides(width: Int, height: Int, override val key: Option[String] = None)(content:VNode*) extends DiffComponent {

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
    rescale(); rerender()
  }

  override def afterAttach(): Unit = {
    super.afterAttach()
    dom.window.addEventListener("resize", rescaleEventListener)
    rescale()
    rerender()
  }

  override def beforeDetach(): Unit = {
    super.beforeDetach()
    dom.window.removeEventListener("resize", rescaleEventListener)
  }

  def slide(n:VNode) = <.div(^.attr("style") := s"height: ${height}px; width: ${width}px; margin: 5px",
    n
  )

  override def render: DiffNode = <.div(^.attr("style") := "position: absolute; width: 100%; height: 100%; background: #d7d8d2",
    <.div(^.attr("style") := s"transform: scale($scale); transform-origin: top left; width: ${width}px; height: ${height}px; background: white; top: ${top}px; left: ${left}px; position: relative",
      Sequencer()(
        content.map(slide):_*
      )
    )
  )

}
