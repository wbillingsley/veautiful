package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful._

object Sequencer {

  def page = {
    VSlides(width=1024, height=680)(Seq(
      <.div("Page One"),
      <.div("Page Two")
    ))
  }


}

case class Sequencer(override val key:Option[String] = None)(var nodes: Seq[VNode], var index:Int = 0) extends DiffComponent with MakeItSo {

  def next():Unit = {
    if (index < nodes.size - 1) {
      index = index + 1
      update()
    }
  }

  def previous():Unit = {
    if (index > 0) {
      index = index - 1
      update()
    }
  }

  def footBox:VNode = {
    <.div(^.cls := "v-sequencer-footbox",
      <.button(^.onClick --> previous, "<"),
      <.span(s" ${index+1} / ${nodes.size} "),
      <.button(^.onClick --> next, ">")
    )
  }

  override def render: DiffNode = <.div(^.cls := "v-sequencer",
    <.div(^.cls := "v-sequencer-inner",
      for {
        (n, i) <- nodes.zipWithIndex
      } yield <.div(
        ^.cls := (if (index == i) "v-sequencer-slide active" else "v-sequencer-slide inactive"),
        n
      )
    ),
    footBox
  )

  override def makeItSo: PartialFunction[MakeItSo, _] = {
    case s:Sequencer =>
      nodes = s.nodes
      index = s.index
      rerender()
  }
}
