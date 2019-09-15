package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful._

object Sequencer {

  def page = Sequencer()(
    <.div("Page One"),
    <.div("Page Two")
  )


}

case class Sequencer(override val key:Option[String] = None)(nodes: VNode*) extends DiffComponent {

  var index = 0

  override def render: DiffNode = <.div(^.cls := "v-sequencer",
    for {
      (n, i) <- nodes.zipWithIndex
    } yield <.div(
      ^.cls := (if (index == i) "v-sequencer active" else "v-sequencer"),
      n
    )
  )

}
