package docs

import com.wbillingsley.veautiful.html.{<, Markup, SVG, VDomNode, ^}

import scala.scalajs.js
import scala.scalajs.js.annotation._

@js.native
@JSImport("highlight.js", JSImport.Default)
object HLJS extends js.Object:
  def highlight(code:String, d:js.Dictionary[String]):js.Dynamic = js.native
  def getLanguage(lang:String):js.Dynamic = js.native

@js.native
@JSImport("marked", "marked")
object Marked extends js.Object:
  def parse(s:String, d:js.Dictionary[js.Function]):String = js.native
  def parseInline(s:String):String = js.native

given marked:Markup = Markup({ (s:String) => Marked.parse(s, js.Dictionary("highlight" -> 
    { (code:String, lang:String) => 
      import scalajs.js.DynamicImplicits.truthValue
      val l = if HLJS.getLanguage(lang) then lang else "plaintext"
      HLJS.highlight(code, js.Dictionary("language" -> l)).value 
    }
  )) 
})

val markedInline:Markup = Markup({ (s:String) => Marked.parseInline(s) })


/**
  * Common UI components to all the views
  */
object Common {

  def markdown(s:String):VDomNode = marked.Fixed(s)
  
  def logoPaths = SVG.g(
    SVG.path(
      ^.attr("style") :=
        """stroke: #ff930080;
          |stroke-width: 7;
          |stroke-linecap: round;
          |fill: transparent;
          |""".stripMargin,
      ^.attr("d") :=
        """M 87 386 l 210 0
          |M 140 362 c -72 -100, -130 -52, -80 -22
          |M 244 362 c 72 -100, 130 -52, 80 -22
          |M 60 280 c -105 -140, -20 -218, 59 -199
          |M 324 280 c 105 -140, 20 -218, -59 -199
          |M 148 88 l 88 0
          |M 169 88 c 0 -20, -12 -50, -42 -50
          |M 192 88 l 0 -80
          |M 215 88 c 0 -20, 12 -50, 42 -50
          |M 165 109 l 54 0
          |M 192 109 l 0 84
          |""".stripMargin
    ),
    SVG.path(
      ^.attr("style") :=
        """stroke: #004479d0;
          |stroke-width: 19;
          |stroke-linecap: round;
          |fill: transparent;
          |""".stripMargin,
      ^.attr("d") :=
        """M 148 159 c 0 0, 0 -50, -50 -50 C 22 111, 16 241, 192 362
          |C 368 241, 362 111, 286 109 c -50 0, -50 50, -50 50
          |""".stripMargin
    )
  )
  
  def logo(w:Int, h:Int) = <.svg(^.attr("viewBox") := "-4 0 396 400", ^.attr("width") := w, ^.attr("height") := h, ^.attr("style") := s"margin: ${h/8}px;",
    logoPaths
  )
  
  def logoWithTitle(w:Int, h:Int) = <.svg(
    ^.attr("viewBox") := "-4 0 396 550", ^.attr("width") := w, ^.attr("height") := h, ^.attr("style") := s"margin: ${h/8}px;",
    logoPaths,
    SVG.g(
      SVG.text(^.attr("x") := 192, ^.attr("y") := 396,
        ^.attr("style") :=
          """font-family: "Times New Roman", serif;
            |font-size: 96px;
            |dominant-baseline: text-before-edge;
            |text-anchor: middle;
            |fill: #004479d0;
            |text-transform: lowercase;
            |font-weight: 400;
            |font-style: italic;
            |""".stripMargin,
        "Veautiful"
      ),
      SVG.path(
        ^.attr("style") :=
          """stroke: #ff930080;
            |stroke-width: 7;
            |fill: transparent;
            |""".stripMargin, ^.attr("d") := "M 82 524 l 210 0"
        
      )
    )
  )

}
