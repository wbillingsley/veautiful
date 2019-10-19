package example

import com.wbillingsley.veautiful.{<, VNode, ^}
import com.wbillingsley.wren.{Circuit, CurrentSource, Orientation, Resistor, VoltageSource}

object WrenExample {

  val circuit = Circuit(Seq(
    new CurrentSource((100, 100)),
    new Resistor((200,100), orientation = Orientation.South),
    new VoltageSource((300, 100), orientation = Orientation.East)
  ), 640, 480)

  def page:VNode =  Common.layout(<.div(^.cls := "row",
    <.div(^.cls := "col", circuit)
  ))
}
