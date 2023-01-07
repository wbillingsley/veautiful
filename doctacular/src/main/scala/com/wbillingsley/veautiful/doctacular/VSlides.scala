package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.html.{<, Styling, DHtmlComponent, VHtmlDiffNode, VHtmlElement, ^, HistoryRouter, PathDSL}
import com.wbillingsley.veautiful.templates.{Sequencer, SequenceItem, templateStyleSuite, WindowScaler, WindowWidthScaler}
import com.wbillingsley.veautiful.Morphing
import com.wbillingsley.veautiful.logging.Logger
import org.scalajs.dom
import org.scalajs.dom.raw.{Event, HTMLElement}

object VSlides {
  val logger = Logger.getLogger(VSlides.getClass)

  trait Layout:
    def apply(slides:VSlides, slide:VHtmlElement, index:Int):VHtmlElement

  object DefaultLayout extends Layout:
    def apply(slides:VSlides, slide:VHtmlElement, index:Int):VHtmlElement =
      <.div(
        ^.cls := s"v-slide ${defaultTheme.className}", ^.attr("style") := s"height: ${slides.height}px", slide
      ).build()

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
  index: Int = 0,
  onIndexChange: Option[Int => Unit] = None,
  sequencerLayout: Sequencer.LayoutFunc = Sequencer.defaultLayout
)


/** VSlides just defines the deck. The layout within the page is part of the definition. */
case class VSlides(
  val width: Int, val height: Int, val content: Seq[SequenceItem], val layout:VSlides.Layout = VSlides.DefaultLayout
) {
  def laidOut = content.zipWithIndex.map { (item, idx)  => layout(this, item, idx) }
}

/** Something that can take a deck, and a page number, and render it to a VDomNode */
type VSlidesPlayer = (VSlides, Int) => VHtmlElement

/**
  * A bare-bones player for VSlides.
  */
case class DefaultVSlidesPlayer(deck: VSlides, override val key: Option[String] = None, scaleToWindow:Boolean = true)(
  index: Int = 0,
  onIndexChange: Option[Int => Unit] = None,
  sequencerLayout: Sequencer.LayoutFunc = Sequencer.defaultLayout
) extends DHtmlComponent with Morphing(VSlidesConfig(index, onIndexChange, sequencerLayout)) {
  
  val morpher = createMorpher(this)

  var top:Double = 0
  var left:Double = 0
  
  private val internalOnIndexChange: Int => Unit = { i =>
    prop.onIndexChange match {
      case Some(f) => f(i)
      case None => updateProp(prop.copy(index = i))
    }
  }

  override def render = {
    val config = prop
    
    val sequencer = Sequencer()(
      deck.laidOut, config.index, layout = config.sequencerLayout, Some(internalOnIndexChange)
    )
    
    <.div(
      WindowScaler(deck.width, deck.height)(
        sequencer, scaleToWindow
      )
    )
  }
  
  def atSlide(i:Int):DefaultVSlidesPlayer = DefaultVSlidesPlayer(deck, key)(i, prop.onIndexChange)
}

object VSlidesMicroRouter {
  enum Route:
    case Gallery
    case Slide(i:Int)

  val slideGalleryBorder = Styling("border: 1px solid #ddd;").register()

  val contentContainerStyle = Styling(
    """width: 100%;
      |margin-left: auto;
      |margin-right: auto;
      |padding-left: 15px;
      |padding-right: 15px;
      |""".stripMargin).withAtRules(
    "@media (min-width: 576px)" -> "max-width: 540px;",
    "@media (min-width: 768px)" -> "max-width: 720px;",
    "@media (min-width: 992px)" -> "max-width: 960px;",
    "@media (min-width: 1200px)" -> "max-width: 1140px;",
  ).register()

  val fsButtonStyle = Styling(
    """border-radius: 5px;
      |background-color: antiquewhite;
      |text-align: center;
      |border: 1px solid #aaa;
      |margin-right: 1rem;
      |""".stripMargin).modifiedBy(
    ":hover" -> "filter: brightness(115%);"
  ).register()
}

/**
  * A micro-router if we want to mount a single deck as the entire site. 
  *
  * @param deck the VSlides deck to mount
  */
case class VSlidesMicroRouter(deck:VSlides) extends HistoryRouter[VSlidesMicroRouter.Route] {

  import VSlidesMicroRouter.Route
  import PathDSL.intParam

  var route = Route.Gallery

  def path(r:Route) = r match {
    case Route.Gallery => "/"
    case Route.Slide(i) => s"/$i"
  }

  def galleryButton = <.button(
    ^.onClick --> routeTo(Route.Gallery), "≡"
  ).build()

  private val internalOnIndexChange: Int => Unit = { i =>
    routeTo(Route.Slide(i))
  }

  def render = route match {
    case Route.Gallery => <.div(

      <.div(^.cls := VSlidesMicroRouter.contentContainerStyle,
        <.div(^.style := "margin: 1em 0;",
          <.button(
            ^.cls := VSlidesMicroRouter.fsButtonStyle,
            ^.on.click --> routeTo(Route.Slide(0)), 
            "Play this deck fullscreen"
          )
        ),

        <.div(^.cls := VSlidesMicroRouter.slideGalleryBorder,
          WindowWidthScaler(deck.width)(
            <.div(deck.laidOut).build(), true
          )
        )
      )
    )

    case Route.Slide(i) => 
      DefaultVSlidesPlayer(deck)(i, sequencerLayout = Sequencer.footBoxLayout(Seq(galleryButton)), onIndexChange = Some(internalOnIndexChange))
  }

  def routeFromLocation(): Route = PathDSL.hashPathList() match {
    case intParam(i) :: _ if i <= deck.content.indices.max => Route.Slide(i)
    case _ => Route.Gallery
  }

  // Ensure the template styles are installed
  templateStyleSuite.install()

}