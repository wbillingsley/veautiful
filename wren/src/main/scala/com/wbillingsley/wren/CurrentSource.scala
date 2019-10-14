package com.wbillingsley.wren
import com.wbillingsley.veautiful.{SVG, VNode, ^}
import com.wbillingsley.wren.Orientation._

class CurrentSource(initial: Option[Double] = None, orientation:Orientation = East) extends Component {

  val current = new Value("A", initial.map((_, QuestionSet)))

  val t1 = new Terminal(orientation.rotate((0, 10), (10, 10)))

  val t2 = new Terminal(orientation.rotate((20, 10), (10, 10)))

  override def terminals: Seq[Terminal] = Seq(t1, t2)

  override def constraints: Seq[Constraint] = Seq(
    EqualityConstraint("Current source", Seq(t1.current, current))
  )

  override def render: VNode = {

    def icon = SVG.g(
      SVG.circle(^.attr("cx") := 10, ^.attr("cy") := 10, ^.attr("r") := 10)
    )

    SVG.g(

    )
  }
}
