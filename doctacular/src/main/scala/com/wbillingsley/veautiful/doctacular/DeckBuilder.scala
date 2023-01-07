package com.wbillingsley.veautiful.doctacular

import com.wbillingsley.veautiful.html.{<, Attacher, RootNode, Markup, VHtmlContent, VHtmlElement, VHtmlBlueprint, ^}
import org.scalajs.dom

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

sealed trait Slide

@JSExportTopLevel("DeckBuilder")
def makeDeckBuilder(parser:(String) => String) = (w:Int, h:Int) => DeckBuilder(w, h)(using Markup { parser })

/**
  * Allows creating VSlides decks using a builder-like notation.
  * 
  * VSlides decks use a fixed resolution for the slides, so that authors don't have to worry about making their slides responsive.
  * The deck then uses a CSS scale transform to fit itself into the available space in the window.
  * 
  * Slides can be added using Markdown or Veautiful for interactive content
  *
  * @param width with of a slide
  * @param height height of a slide
  * @param slides any initial content to initialise the builder with
  * @param markup the markup engine to use in Markdown slides
  */
class DeckBuilder(width:Int = 1920, height:Int = 1080, slides:List[Seq[() => VHtmlContent]] = Nil)(using markup:Markup) {

  def stripIndent(s:String):String = {
    val lines = s.split('\n')
    val indents = for { l <- lines if !l.trim().isEmpty } yield l.indexWhere(!_.isWhitespace)
    val minIndent = indents.min

    val stripped = (for {
      l <- lines
    } yield l.drop(minIndent)).mkString("\n")

    stripped
  }

  @JSExport
  def markdownSlide(m:String):DeckBuilder = new DeckBuilder(width, height, Seq(() => markup.Fixed(stripIndent(m))) :: slides)

  def veautifulSlide(vnode:VHtmlContent):DeckBuilder = new DeckBuilder(width, height, Seq(() => vnode) :: slides)

  @JSExport
  def markdownSlides(m:String):DeckBuilder = {
    val pattern = """(?:^|[\n\r\u0085\u2028\u2029])(---)(?=[\n\r\u0085\u2028\u2029]|$)""".r
    val lines = pattern.split(stripIndent(m))
    new DeckBuilder(width, height, lines.toSeq.map(l => () => markup.Fixed(l)) :: slides)
  }

  @JSExport
  def withClass(c:String):DeckBuilder = slides match {
    case Nil => this
    case h :: t => new DeckBuilder(width, height, (for { s <- h } yield () => <.div(^.cls := c, s())) :: t)
  }

  def renderSlides:VSlides = {
    VSlides(width, height, for slide <- slides.reverse.flatten yield slide.apply() match { 
      case e:VHtmlElement @unchecked => e
      case b:VHtmlBlueprint @unchecked => b.build()
    })
  }
  
  def renderNode(using player:VSlidesPlayer = { (slides, index) => DefaultVSlidesPlayer(slides)(index=index)}) = player.apply(renderSlides, 0)

  @JSExport
  def render(selector:String) = {
    val slides = renderNode(using { (slides, index) => DefaultVSlidesPlayer(slides)(index=index)})
    DeckBuilder.publishedDecks(selector) = slides
    val a = Attacher.newRoot(dom.document.querySelector(selector))
    a.render(slides)
  }

  /** Creates a micro-router that has a gallery view and a view for each slide, and mounts it to the root */
  def mountToRoot(root:RootNode) = {
    val deck = renderSlides
    val microRouter = VSlidesMicroRouter(deck)
    root.render(microRouter)
  }

}

@JSExportTopLevel("DeckBuilderCompanion")
object DeckBuilder {

  val publishedDecks:mutable.Map[String, VHtmlElement] = mutable.Map.empty

}