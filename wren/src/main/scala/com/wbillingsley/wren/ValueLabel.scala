package com.wbillingsley.wren
import com.wbillingsley.veautiful.html.{<, SVG, VHtmlDiffNode, ^}

class ValueLabel(v:Value, pos:(Int, Int), vertical:Boolean = false) extends Component {

  def render: VHtmlDiffNode = {
    val (x, y) = pos

    v.value match {
      case Some((value, provenance)) => SVG.text(^.attr("x") := x, ^.attr("y") := y, v.stringify)
      case _ => SVG.text(^.attr("x") := x, ^.attr("y") := y, "Not set")
    }
  }

  override def terminals: Seq[Terminal] = Seq.empty

  override def constraints: Seq[Constraint] = Seq.empty
}
