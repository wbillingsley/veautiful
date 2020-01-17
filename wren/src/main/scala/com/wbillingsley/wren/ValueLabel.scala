package com.wbillingsley.wren
import com.wbillingsley.veautiful.html.{<, DElement, SVG, VHtmlDiffNode, VHtmlNode, ^}

import scala.scalajs.js.|

class ValueLabel(name:(String, String), v:Value, pos:(Int, Int), anchorClass:String = "left ") extends Component {

  def render: VHtmlDiffNode = {
    val (x, y) = pos
    val (n, sub) = name

    v.value match {
      case Some((value, provenance)) =>
        SVG.text(^.attr("x") := x, ^.attr("y") := y, ^.cls := anchorClass,
          n, SVG.tspan(^.attr("dy") := 5, sub), SVG.tspan(^.attr("dy") := -5, " = ", v.stringify)
        )

      case _ =>
        SVG.text(^.attr("x") := x, ^.attr("y") := y, ^.cls := anchorClass,
          n, SVG.tspan(^.attr("dy") := 5, sub)
        )

    }
  }

  override def terminals: Seq[Terminal] = Seq.empty

  override def constraints: Seq[Constraint] = Seq.empty
}

object ValueLabel {


}