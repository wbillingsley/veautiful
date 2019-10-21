package com.wbillingsley.wren

import com.wbillingsley.veautiful.{SVG, ^}
import com.wbillingsley.wren.Orientation.East
import Orientation._


class Resistor(pos:(Int,Int), orientation:Orientation = East, initial: Option[Double] = None) extends Component {

  val r = 20

  val current = new Value("A", initial.map((_, QuestionSet)))

  val t1 = new Terminal(orientation.rotate((-r - 1, 0), (0, 0)) + pos)

  val t2 = new Terminal(orientation.rotate((r + 1, 0), (0, 0)) + pos)

  override def terminals: Seq[Terminal] = Seq(t1, t2)

  override def constraints: Seq[Constraint] = Seq(
    SumConstraint("Kirchhoff's Current Law", Seq(t1.current, t2.current), 0)
  )

  val path = "M -20 0 l 4 0 l 4 -8 l 8 16 l 8 -16 l 8 16 l 4 -8 l 4 0"

  override def render = {

    def icon = SVG.g(^.attr("transform") := s"rotate(${orientation.deg})",
      SVG.path(^.attr("d") := path)
    )

    val (x, y) = pos

    SVG.g(^.cls := "wren-component current-source", ^.attr("transform") := s"translate($x, $y)",
      icon
    )
  }
}