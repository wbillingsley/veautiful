package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.html.{<, DHtmlComponent, VHtmlDiffNode, VDomNode, ^, Styling}
import com.wbillingsley.veautiful.logging.Logger
import org.scalajs.dom


object WindowWidthScaler {

  /** The surround, that contains the slide deck */
  val scalerTopStyle = Styling(
    """position: relative;
      |height: 100%;
      |background: #d7d8d2;
      |overflow-x: hidden;
      |""".stripMargin).modifiedBy(
    ".unscaled" -> "overflow-x: inherit;"
  ).register()

  val scalerStyle = Styling(
    """transform-origin: top left;
      |position: relative;
      |box-shadow: 0 0 30px #888;
      |overflow: hidden;
      |""".stripMargin).register()

}

case class WindowWidthScaler(width: Int)(content:VDomNode, scaleToWindow:Boolean = true) extends DHtmlComponent with Morphing(content, scaleToWindow) {
  
  import WindowWidthScaler._

  val logger = Logger.getLogger(WindowWidthScaler.getClass)

  val morpher = createMorpher(this)

  var scale:Double = 1
  var left:Double = 0

  def rescale() = for { n <- domNode } {
    val r = n.getBoundingClientRect()
    scale = r.width / width

    left = Math.max((r.width - scale * width) / 2, 0)

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

  override def render = {
    val (content, scaleToWindow) = prop

    <.div(^.cls := (if (scaleToWindow) s"${scalerTopStyle.className} window-width-scaler-top scaled" else s"${scalerTopStyle.className} window-scaler-top unscaled"),
      <.div(^.cls := (if (scaleToWindow) s"${scalerStyle.className} window-width-scaler scaled" else s"${scalerStyle.className} window-scaler unscaled"),
        ^.attr("style") := (if (scaleToWindow) {
          s"transform: scale($scale); width: ${width}px; left: ${left}px; "
        } else {
          s"width: ${width}px; "
        }),
        content
      )
    )
  }

}