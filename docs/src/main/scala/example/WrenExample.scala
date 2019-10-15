package example

import com.wbillingsley.veautiful.{<, VNode, ^}
import com.wbillingsley.wren.{Circuit, CurrentSource}

object WrenExample {

  val circuit = Circuit(Seq(new CurrentSource((100, 100))), 640, 480)

  def page:VNode =  Common.layout(<.div(^.cls := "row",
    <.div(^.cls := "col", circuit)
  ))
}
