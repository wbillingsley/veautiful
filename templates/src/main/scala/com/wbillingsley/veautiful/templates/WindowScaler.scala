package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^, Styling}
import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.templates.WindowScaler.{scalerStyle, scalerTopStyle}
import org.scalajs.dom

object WindowScaler {

  /** The surround, that contains the slide deck */
  val scalerTopStyle = Styling(
    """position: absolute;
      |width: 100%;
      |height: 100%;
      |background: #d7d8d2;
      |overflow: hidden;
      |""".stripMargin).modifiedBy(
    ".unscaled" -> "overflow: inherit;"
  ).register()

  val scalerStyle = Styling(
    """transform-origin: top left;
      |position: relative;
      |box-shadow: 0 0 30px #888;
      |overflow: hidden;
      |""".stripMargin).register()
  
}

case class WindowScaler(width: Int, height: Int)(content:VHtmlNode, scaleToWindow:Boolean = true) extends VHtmlComponent with Morphing(content, scaleToWindow) {

  val logger = Logger.getLogger(WindowScaler.getClass)

  val morpher = createMorpher(this)
  
  var scale:Double = 1
  var top:Double = 0
  var left:Double = 0

  def rescale() = for { n <- domNode } {
    val r = n.asInstanceOf[dom.raw.HTMLElement].getBoundingClientRect()
    scale = Math.min(r.height / height, r.width / width)

    left = Math.max((r.width - scale * width) / 2, 0)
    top = Math.max((r.height - scale * height) / 2, 0)

    logger.debug(s"Scale is now $scale")
  }
  
  val rescaleEventListener: (e:dom.Event) => Unit = { (_) =>
    rescale()
    rerender()
  }

  override def afterAttach(): Unit = {
    super.afterAttach()
    dom.window.addEventListener("resize", rescaleEventListener)
    logger.debug(s"$this attached")
    
    rescale()
  }

  override def beforeDetach(): Unit = {
    super.beforeDetach()
    dom.window.removeEventListener("resize", rescaleEventListener)
  }

  override def render: VHtmlDiffNode = {
    val (content, scaleToWindow) = prop
    
    <.div(^.cls := (if (scaleToWindow) s"${scalerTopStyle.className} window-scaler-top scaled" else s"${scalerTopStyle.className} window-scaler-top unscaled"),
      <.div(^.cls := (if (scaleToWindow) s"${scalerStyle.className} window-scaler scaled" else s"${scalerStyle.className} window-scaler unscaled"),
        ^.attr("style") := (if (scaleToWindow) {
          s"transform: scale($scale); width: ${width}px; height: ${height}px; top: ${top}px; left: ${left}px; "
        } else {
          s"width: ${width}px; height: ${height}px; "
        }),
        content
      )
    )
  }
  
}
