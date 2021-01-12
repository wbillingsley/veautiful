package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.html.{<, Styling, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^}
import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.templates.Sequencer.LayoutFunc
import com.wbillingsley.veautiful.templates.VSlides.{slidesScalerStyle, vslidesTopStyle}
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

object VSlides {
  val logger = Logger.getLogger(VSlides.getClass)

  def defaultLayout:LayoutFunc = { (sequencer, s, _) =>
    <.div(
      ^.cls := s"v-slide ${defaultTheme.className}", s, sequencer.footBox
    )
  }

  /*
   * Functional styles, e.g. for rescaling
   */
  
  /** The surround, that contains the slide deck */
  val vslidesTopStyle = Styling(
    """position: absolute;
      |width: 100%;
      |height: 100%;
      |background: #d7d8d2;
      |overflow: hidden;
      |""".stripMargin).modifiedBy(
    ".unscaled" -> "overflow: inherit;"
  ).register()
  
  val slidesScalerStyle = Styling(
    """transform-origin: top left;
      |position: relative;
      |box-shadow: 0 0 30px #888;
      |overflow: hidden;
      |""".stripMargin).register()
  
  val defaultTheme = Styling(
    """background: white;
      |padding: 50px;
      |display: flex;
      |height: 100%;
      |font-family: sans-serif;
      |font-weight: normal;
      |font-size: 20px;
      |""".stripMargin).modifiedBy(
    "> *:first-child" -> "width: 100%",
    " li" -> "line-height: 200%;",
    " li pre" -> "line-height: 125%;",
    " img" -> "max-height: 75; max-width: 100%;",
    " table > thead > tr > th" -> "text-align: left; padding: 8px;",
    " table > tbody > tr > td" -> "padding: 8px;",
    " table > tbody > tr:nth-of-type(odd)" -> "background-color: #f9f9f9;",
    " a" -> "text-decoration: none;",
    " blockquote" -> "padding: 15px; border-left: 10px solid #aaa;",
    " .center" -> "text-align: center;",
    " .middle" -> "margin-top: auto; margin-bottom: auto;",
    " .byline" -> "color: #aaaaaa;",
    " .notes-only" -> "display: none",
    " .footnote" ->
      """position: fixed;
        |text-align: left;
        |bottom: 10px;
        |left: 20px;
        |font-family: sans-serif;
        |font-size: 18px;
        |color: rgb(120, 120, 120);
        |margin-right: auto;
        |margin-left: auto;
        |""".stripMargin,
    " h1" ->
      """font-family: 'Times New Roman', serif;
        |color: #7d5177;
        |font-weight: normal;
        |margin-top: 0;
        |margin-block-end: 1em;
        |font-variant-caps: normal;
        |""".stripMargin,
    " h2" ->
      """font-family: 'Times New Roman', serif;
        |color: #7d5177;
        |font-weight: normal;
        |margin-top: 0;
        |margin-block-end: 1em;
        |font-variant-caps: normal;
        |""".stripMargin,
    " h3" ->
      """font-family: 'Times New Roman', serif;
        |color: #7d5177;
        |font-weight: normal;
        |margin-top: 0;
        |margin-block-end: 1em;
        |font-variant-caps: normal;
        |""".stripMargin,
    
  ).register()
  
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
    rescale()
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

    <.div(^.cls := (if (scaleToWindow) s"${vslidesTopStyle.className} vslides-top scaled" else s"${vslidesTopStyle.className} vslides-top unscaled"),
      <.div(^.cls := (if (scaleToWindow) s"${slidesScalerStyle.className} vslides-scaler scaled" else s"${slidesScalerStyle.className} vslides-scaler unscaled"),
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
