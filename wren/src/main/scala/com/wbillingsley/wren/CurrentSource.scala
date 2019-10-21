package com.wbillingsley.wren
import com.wbillingsley.veautiful.{SVG, VNode, ^}
import com.wbillingsley.wren.Orientation._

class CurrentSource(pos:(Int,Int), orientation:Orientation = East, initial: Option[Double] = None) extends Component {

  val r = 20

  val current = new Value("A", initial.map((_, QuestionSet)))

  val voltage = new Value("V")

  val t1 = new Terminal(orientation.rotate((-r - 1, 0), (0, 0)) + pos)

  val t2 = new Terminal(orientation.rotate((r + 1, 0), (0, 0)) + pos)

  override def terminals: Seq[Terminal] = Seq(t1, t2)

  override def constraints: Seq[Constraint] = Seq(
    EqualityConstraint("Current source", Seq(t1.current, current)),
    SumConstraint("Kirchhoff's Current Law", Seq(t1.current, t2.current), 0)
  )

  override def render = {

    def icon = SVG.g(
      SVG.circle(^.attr("cx") := 0, ^.attr("cy") := 0, ^.attr("r") := r),
      SVG.text(^.attr("x") := 0, ^.attr("y") := 0, ^.cls := "centre middle current-source-arrow", orientation match {
        case Orientation.East => "→"
        case Orientation.West => "←"
        case Orientation.North => "↑"
        case Orientation.South => "↓"
      })
    )

    val (x, y) = pos

    SVG.g(^.cls := "wren-component current-source", ^.attr("transform") := s"translate($x, $y)",
      icon
    )
  }
}
