package com.wbillingsley.wren
import com.wbillingsley.veautiful.{SVG, VNode, ^}
import com.wbillingsley.wren.Orientation._

class CurrentSource(pos:(Int,Int), orientation:Orientation = East, initial: Option[Double] = None) extends Component {

  val r = 20

  val current = new Value("A", initial.map((_, QuestionSet)))

  val voltage = new Value("V")

  val t1 = new Terminal(pos + orientation.rotate((-1, r), (r, r)))

  val t2 = new Terminal(pos + orientation.rotate((2 * r + 1, r), (r, r)))

  override def terminals: Seq[Terminal] = Seq(t1, t2)

  override def constraints: Seq[Constraint] = Seq(
    EqualityConstraint("Current source", Seq(t1.current, current))
  )

  override def render = {

    def icon = SVG.g(
      SVG.circle(^.attr("cx") := r, ^.attr("cy") := r, ^.attr("r") := r),
      SVG.text(^.attr("x") := r, ^.attr("y") := r, ^.cls := "centre middle current-source-arrow", orientation match {
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
