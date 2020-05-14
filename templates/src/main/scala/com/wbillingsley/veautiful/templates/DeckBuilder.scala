package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful.html.{<, Attacher, Markup, VHtmlNode, ^}
import org.scalajs.dom

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

sealed trait Slide

/**
  *
  */
@JSExportTopLevel("DeckBuilder")
class DeckBuilder(width:Int = 1920, height:Int = 1080, slides:List[Seq[() => VHtmlNode]] = Nil) {

  import DeckBuilder.markdownGenerator

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
  def markdownSlide(m:String):DeckBuilder = new DeckBuilder(width, height, Seq(() => markdownGenerator.Fixed(stripIndent(m))) :: slides)

  def veautifulSlide(vnode:VHtmlNode):DeckBuilder = new DeckBuilder(width, height, Seq(() => vnode) :: slides)


  @JSExport
  def markdownSlides(m:String):DeckBuilder = {
    val lines = stripIndent(m).split("\n---\n")
    lines.foreach(println)
    new DeckBuilder(width, height, lines.toSeq.map(l => () => markdownGenerator.Fixed(l)) :: slides)
  }

  @JSExport
  def withClass(c:String):DeckBuilder = slides match {
    case Nil => this
    case h :: t => new DeckBuilder(width, height, (for { s <- h } yield () => <.div(^.cls := c, s())) :: t)
  }

  def renderNode:VSlides = {
    new VSlides(width, height)(
      slides.reverse.flatten.map(_.apply)
    )
  }

  @JSExport
  def render(selector:String) = {
    val slides = renderNode
    DeckBuilder.publishedDecks(selector) = slides
    val a = Attacher.newRoot(dom.document.querySelector(selector))
    a.render(slides)
  }

}

@JSExportTopLevel("DeckBuilderCompanion")
object DeckBuilder {

  var markdownGenerator = new Markup({ s:String => js.Dynamic.global.marked(s).asInstanceOf[String] })

  val publishedDecks:mutable.Map[String, VSlides] = mutable.Map.empty

}