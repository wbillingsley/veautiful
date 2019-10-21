package example

import com.wbillingsley.veautiful.{<, VNode, ^}
import com.wbillingsley.wren.{Circuit, CurrentSource, Orientation, Resistor, VoltageSource, Wire}

object WrenExample {

  val cs = new CurrentSource((400, 100))
  val vs = new VoltageSource((100, 150), orientation = Orientation.North)
  val r1 = new Resistor((300,100), orientation = Orientation.South)
  val r2 = new Resistor((300,200), orientation = Orientation.South)

  val circuit = Circuit(Seq(
    cs, r1, r2, vs,
    new Wire(vs.t2, r1.t1, 100 -> 50, 300 -> 50),
    new Wire(r1.t2, r2.t1),
    new Wire(r2.t2, vs.t1, 300 -> 300, 100 -> 300)
  ), 640, 480)

  def page:VNode =  Common.layout(<.div(^.cls := "row",
    <.div(^.cls := "col", circuit)
  ))
}
