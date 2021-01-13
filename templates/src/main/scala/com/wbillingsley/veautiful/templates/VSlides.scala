package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.html.{<, Styling, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^}
import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.templates.Sequencer.LayoutFunc
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

object VSlides {
  val logger = Logger.getLogger(VSlides.getClass)

  def defaultLayout:LayoutFunc = { (sequencer, s, _) =>
    <.div(
      ^.cls := s"v-slide ${defaultTheme.className}", s
    )
  }

  /*
   * Functional styles, e.g. for rescaling
   */
  
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
  deck: VSlides,
  index: Int = 0,
  onIndexChange: Option[Int => Unit] = None,
)


/** VSlides just defines the deck. The layout within the page is part of the definition. */
case class VSlides(
  width: Int, height: Int, content: Seq[SequenceItem], layout:Sequencer.LayoutFunc = VSlides.defaultLayout
)

/** Something that can take a deck, and a page number, and render it to a VHtmlNode */
type VSlidesPlayer = (VSlides, Int) => VHtmlNode

case class DefaultVSlidesPlayer(width: Int, height: Int, override val key: Option[String] = None, scaleToWindow:Boolean = true)(
  deck: VSlides,
  index: Int = 0,
  onIndexChange: Option[Int => Unit] = None
) extends VHtmlComponent with Morphing(VSlidesConfig(deck, index, onIndexChange)) {
  
  val morpher = createMorpher(this)

  var top:Double = 0
  var left:Double = 0
  
  private val internalOnIndexChange: Int => Unit = { i =>
    prop.onIndexChange match {
      case Some(f) => f(i)
      case None => updateProp(prop.copy(index = i))
    }
  }

  override def render: VHtmlDiffNode = {
    val config = prop
    
    val sequencer = Sequencer()(
      config.deck.content, config.index, layout = config.deck.layout, Some(internalOnIndexChange)
    )
    
    <.div(
      WindowScaler(width, height)(
        <.div(sequencer, sequencer.footBox), scaleToWindow
      )
    )
  }
  
  def atSlide(i:Int):DefaultVSlidesPlayer = DefaultVSlidesPlayer(width, height, key)(prop.deck, i, prop.onIndexChange)
}
