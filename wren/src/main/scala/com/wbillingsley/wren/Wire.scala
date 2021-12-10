package com.wbillingsley.wren

import com.wbillingsley.veautiful.html.{SVG, ^}

class Wire(t1:Connector, t2:Connector, via:(Int,Int)*) extends Component {

  t1.connect(this)
  t2.connect(this)

  override def terminals: Seq[Terminal] = Seq.empty

  override def constraints: Seq[Constraint] = Seq.empty

  def path = {
    val (x1, y1) = t1.pos
    val (x2, y2) = t2.pos

    s"M $x1 $y1 " +
      via.map({ case (x, y) => s"L $x $y"}).mkString(" ") +
      s" L $x2 $y2"
  }

  override def render = {

    SVG.g(^.cls := "wren-component wire",
      SVG.path(^.attr("d") := path)
    )
  }
}