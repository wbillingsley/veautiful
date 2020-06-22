package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^}
import com.wbillingsley.veautiful.MakeItSo
import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.templates.Sequencer.LayoutFunc
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

object VSlides {
  val logger = Logger.getLogger(VSlides.getClass)

  def defaultLayout:LayoutFunc = { case (sequencer, s, _) =>
    <.div(
      ^.cls := "v-slide",
      s.content,
      sequencer.footBox
    )
  }

}

case class VSlides(width: Int, height: Int, override val key: Option[String] = None, scaleToWindow:Boolean = true)(
  var content:Seq[SequenceItem], var index:Int = 0, var layout:Sequencer.LayoutFunc = VSlides.defaultLayout,
  onIndexChange: Option[Int => Unit] = None
) extends VHtmlComponent with MakeItSo {

  var scale:Double = 1
  var top:Double = 0
  var left:Double = 0

  def rescale() = for { n <- domNode } {
    val r = n.asInstanceOf[HTMLElement].getBoundingClientRect()
    scale = Math.min(r.height / height, r.width / width)

    left = Math.max((r.width - scale * width) / 2, 0)
    top = Math.max((r.height - scale * height) / 2, 0)

    VSlides.logger.debug(s"Scale is now $scale")
  }

  def rescaleEventListener(e:Event):Unit = {
    rerender()
  }

  override def afterAttach(): Unit = {
    super.afterAttach()
    dom.window.addEventListener("resize", rescaleEventListener)
    VSlides.logger.debug(s"$this attached")
  }

  override def beforeDetach(): Unit = {
    super.beforeDetach()
    dom.window.removeEventListener("resize", rescaleEventListener)
  }

  override def render: VHtmlDiffNode = {
    rescale()

    <.div(^.cls := (if (scaleToWindow) "vslides-top scaled" else "vslides-top unscaled"),
      <.div(^.cls := (if (scaleToWindow) "vslides-scaler scaled" else "vslides-scaler unscaled"),
        ^.attr("style") := (if (scaleToWindow) {
          s"transform: scale($scale); width: ${width}px; height: ${height}px; top: ${top}px; left: ${left}px; "
        } else {
          s"width: ${width}px; height: ${height}px; "
        }),
        Sequencer()(
          content, index, layout = layout, onIndexChange
        )
      )
    )
  }

  override def makeItSo: PartialFunction[MakeItSo, _] = {
    case v:VSlides =>
      content = v.content
      index = v.index
      layout = v.layout
      rerender()
  }

  def atSlide(i:Int):VSlides = {
    index = i
    this
  }
}
