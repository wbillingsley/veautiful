package com.wbillingsley.wren

import com.wbillingsley.veautiful.html.{SVG, ^}
import com.wbillingsley.wren.Orientation.East
import com.wbillingsley.wren.Orientation._

class VoltageSource(pos:(Int,Int), orientation:Orientation = East, initial: Option[Double] = None) extends Component {

  val r = 20

  val voltage = new Value("V", initial.map((_, QuestionSet)))

  val t1 = new Terminal(orientation.rotate((-r - 1, 0), (0, 0)) + pos)

  val t2 = new Terminal(orientation.rotate((r + 1, 0), (0, 0)) + pos)

  override def terminals: Seq[Terminal] = Seq(t1, t2)

  override def constraints: Seq[Constraint] = Seq(
    SumConstraint("Kirchhoff's Current Law", Seq(t1.current, t2.current), 0)
  )

  override def render = {

    val (x1, y1) = orientation.rotate((-r/2, 0), (0,0))
    val (x2, y2) = orientation.rotate((r/2, 0), (0,0))
    
    def icon = SVG.g(
      SVG.circle(^.attr("cx") := 0, ^.attr("cy") := 0, ^.attr("r") := r),
      SVG.text(^.attr("x") := x1, ^.attr("y") := y1, ^.cls := "centre middle voltage-source-sign", "-"),
      SVG.text(^.attr("x") := x2, ^.attr("y") := y2, ^.cls := "centre middle voltage-source-sign", "+")
    )

    val (x, y) = pos

    SVG.g(^.cls := "wren-component voltage-source", ^.attr("transform") := s"translate($x, $y)",
      icon
    )
  }
}