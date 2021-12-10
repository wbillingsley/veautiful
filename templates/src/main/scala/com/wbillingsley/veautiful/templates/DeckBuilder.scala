package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.html.{<, Attacher, Markup, VHtmlNode, ^}
import org.scalajs.dom

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

sealed trait Slide

@JSExportTopLevel("DeckBuilder")
def makeDeckBuilder(parser:(String) => String) = (w:Int, h:Int) => DeckBuilder(w, h)(using Markup { parser })

class DeckBuilder(width:Int = 1920, height:Int = 1080, slides:List[Seq[() => VHtmlNode]] = Nil)(using markup:Markup) {

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

  def veautifulSlide(vnode:VHtmlNode):DeckBuilder = new DeckBuilder(width, height, Seq(() => vnode) :: slides)

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
    VSlides(width, height, slides.reverse.flatten.map(_.apply))
  }
  
  def renderNode(using player:VSlidesPlayer = { (slides, index) => DefaultVSlidesPlayer(slides)(index=index)}) = player.apply(renderSlides, 0)

  @JSExport
  def render(selector:String) = {
    val slides = renderNode(using { (slides, index) => DefaultVSlidesPlayer(slides)(index=index)})
    DeckBuilder.publishedDecks(selector) = slides
    val a = Attacher.newRoot(dom.document.querySelector(selector))
    a.render(slides)
  }

}

@JSExportTopLevel("DeckBuilderCompanion")
object DeckBuilder {

  val publishedDecks:mutable.Map[String, VHtmlNode] = mutable.Map.empty

}