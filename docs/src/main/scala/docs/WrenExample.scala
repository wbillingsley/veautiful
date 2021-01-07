package docs

import com.wbillingsley.veautiful.html.{<, VHtmlNode, ^}
import com.wbillingsley.wren._

object WrenExample {

  val cs = new CurrentSource((400, 100), initial = Some(0.2))
  val vs = new VoltageSource((100, 150), orientation = Orientation.North, initial = Some(5))
  val r1 = new Resistor((300,100), orientation = Orientation.South)
  val r2 = new Resistor((300,200), orientation = Orientation.South)

  val circuit:Circuit = Circuit(
    Seq(
      r1, r2, vs,
      new Wire(vs.t2, r1.t1, 100 -> 50, 300 -> 50),
      new Wire(r1.t2, r2.t1),
      new Wire(r2.t2, vs.t1, 300 -> 300, 100 -> 300),
      new ValueLabel("V" -> "cc", vs.voltage, (30, 150)),
      new ValueSlider(vs.voltage, (0, 160), min = "1", max = "5", onUpdate = onUpdate),
      new ValueLabel("R" -> "1", r1.resistance, (330, 100)),
      new ValueSlider(r1.resistance, (330, 110), min = "0.25", max = "47000", onUpdate = onUpdate),
      new ValueLabel("R" -> "2", r2.resistance, (330, 200)),
      new ValueSlider(r2.resistance, (330, 210), min = "0.25", max = "47000", onUpdate = onUpdate),
      new ValueLabel("V" -> "1", r1.voltage, (250, 100), "right"),
      new ValueLabel("V" -> "2", r2.voltage, (250, 200), "right", symbol = Seq(ValueLabel.voltageMarkers(280 -> 170, 280 -> 230))),
      new ValueLabel("I" -> "1", r1.t1.current, (150, 10), "middle", symbol = Seq(ValueLabel.currentArrow((140, 30), Orientation.East)))
    ),
    640, 480
  )

  val propagator:ConstraintPropagator = ConstraintPropagator(circuit.components.flatMap(_.constraints) ++ Seq(
    EquationConstraint("Kirchhoff's Voltage Law", Seq(
      vs.voltage -> (() => for { (v1, _) <- r1.voltage.value; (v2, _) <- r2.voltage.value } yield v1 + v2),
      r1.voltage -> (() => for { (vcc, _) <- vs.voltage.value; (v2, _) <- r2.voltage.value } yield vcc - v2),
      r2.voltage -> (() => for { (v1, _) <- r1.voltage.value; (vcc, _) <- vs.voltage.value } yield vcc - v1),
    )),
    EquationConstraint("Ohm's Law", Seq(
      r1.t1.current -> (() => for {
        (ra, _) <- r1.resistance.value
        (rb, _) <- r2.resistance.value
        (vcc, _) <- vs.voltage.value
      } yield vcc / (ra + rb))
    ))
  ))

  def onUpdate():Unit = {
    propagator.clearCalculations()
    propagator.resolve()
    circuit.rerender()
  }

  def page:VHtmlNode = <.div(^.cls := "row",
    <.div(^.cls := "col", circuit)
  )


}
