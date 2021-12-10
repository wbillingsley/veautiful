package com.wbillingsley.wren
import com.wbillingsley.veautiful.html.{<, DElement, SVG, VHtmlDiffNode, VHtmlNode, ^}

import scala.scalajs.js.|

class ValueLabel(name:(String, String), v:Value, pos:(Int, Int), anchorClass:String = "left ", symbol:Seq[VHtmlNode] = Seq.empty) extends Component {

  def render: VHtmlDiffNode = {
    val (x, y) = pos
    val (n, sub) = name

    SVG.g(
      v.value match {
        case Some((value, provenance)) =>
          SVG.text(^.attr("x") := x, ^.attr("y") := y, ^.cls := anchorClass,
            n, SVG.tspan(^.attr("dy") := 5, sub), SVG.tspan(^.attr("dy") := -5, " = ", v.stringify)
          )

        case _ =>
          SVG.text(^.attr("x") := x, ^.attr("y") := y, ^.cls := anchorClass,
            n, SVG.tspan(^.attr("dy") := 5, sub)
          )
      },
      symbol
    )
  }

  override def terminals: Seq[Terminal] = Seq.empty

  override def constraints: Seq[Constraint] = Seq.empty
}

object ValueLabel {

  def arrow(stemLength:Int, headWidth:Int, headLength:Int) = {
    s"M 0 0 l $stemLength 0 l ${-headLength} ${-headWidth} M $stemLength 0 l ${-headLength} ${headWidth}"
  }

  def currentArrow(pos:(Int, Int), direction:Orientation):VHtmlNode = {
    val (x, y) = pos

    SVG.g(^.cls := "current-arrow", ^.attr("transform") := s"translate(${x}, ${y}) rotate(${direction.deg})",
      SVG.path(^.attr("d") := arrow(20, 6, 8))
    )
  }

  def voltageMarkers(pos:(Int, Int), neg:(Int, Int)):VHtmlNode = {
    val (x1, y1) = pos
    val (x2, y2) = neg

    SVG.g(^.cls := "voltage-markers",
      SVG.text(^.attr("x") := x1, ^.attr("y") := y1, "+"),
      SVG.text(^.attr("x") := x2, ^.attr("y") := y2, "-")
    )
  }

}