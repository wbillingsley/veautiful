package example

import com.wbillingsley.veautiful.html.{<, Markup, SVG, VHtmlNode, ^}

import scala.scalajs.js

/**
  * Common UI components to all the views
  */
object Common {

  val markdownGenerator = new Markup({ (s:String) => js.Dynamic.global.marked(s).asInstanceOf[String] })

  def markdown(s:String):VHtmlNode = markdownGenerator.Fixed(s)
  
  def logoPaths = SVG.g(
    SVG.path(
      ^.attr("style") :=
        """stroke: #ff930080;
          |stroke-width: 7;
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
          |M 126 234 l 14 14 l 27 0 l 25 -25 l 25 25 l 27 0 l 14 -14
          |M 139 273 l 106 0
          |M 192 224 l 0 80
          |""".stripMargin
    ),
    SVG.path(
      ^.attr("style") :=
        """stroke: #004479d0;
          |stroke-width: 19;
          |fill: transparent;
          |""".stripMargin,
      ^.attr("d") :=
        """M 118 179 C 158 179, 166 104, 98 109 C 22 111, 16 241, 192 362
          |C 368 241, 362 111, 286 109 C 218 104, 226 179, 266 179
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
