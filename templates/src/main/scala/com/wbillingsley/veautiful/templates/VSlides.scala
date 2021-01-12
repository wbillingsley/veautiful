package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^}
import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.templates.Sequencer.LayoutFunc
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

object VSlides {
  val logger = Logger.getLogger(VSlides.getClass)

  def defaultLayout:LayoutFunc = { (sequencer, s, _) =>
    <.div(
      ^.cls := "v-slide", s, sequencer.footBox
    )
  }

}

case class VSlidesConfig(
  content: Seq[SequenceItem],
  index: Int = 0,
  layout:Sequencer.LayoutFunc = VSlides.defaultLayout,
  onIndexChange: Option[Int => Unit] = None,
)

case class VSlides(width: Int, height: Int, override val key: Option[String] = None, scaleToWindow:Boolean = true)(
  content: Seq[SequenceItem],
  index: Int = 0,
  layout:Sequencer.LayoutFunc = VSlides.defaultLayout,
  onIndexChange: Option[Int => Unit] = None
) extends VHtmlComponent with Morphing(VSlidesConfig(content, index, layout, onIndexChange)) {
  
  val morpher = createMorpher(this)

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

  val rescaleEventListener: (e:Event) => Unit = { (_) => 
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
  
  private val internalOnIndexChange: Int => Unit = { i =>
    prop.onIndexChange match {
      case Some(f) => f(i)
      case None => updateProp(prop.copy(index = i))
    }
  }

  override def render: VHtmlDiffNode = {
    rescale()
    
    val config = prop

    <.div(^.cls := (if (scaleToWindow) "vslides-top scaled" else "vslides-top unscaled"),
      <.div(^.cls := (if (scaleToWindow) "vslides-scaler scaled" else "vslides-scaler unscaled"),
        ^.attr("style") := (if (scaleToWindow) {
          s"transform: scale($scale); width: ${width}px; height: ${height}px; top: ${top}px; left: ${left}px; "
        } else {
          s"width: ${width}px; height: ${height}px; "
        }),
        Sequencer()(
          config.content, config.index, layout = config.layout, Some(internalOnIndexChange)
        )
      )
    )
  }
  
  def atSlide(i:Int):VSlides = VSlides(width, height, key)(prop.content, i, prop.layout, prop.onIndexChange)
}
