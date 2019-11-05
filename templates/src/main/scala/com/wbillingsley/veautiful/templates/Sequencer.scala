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

case class Sequencer(override val key:Option[String] = None)(var nodes: Seq[SequenceItem], var index:Int = 0) extends DiffComponent with MakeItSo {

  def next():Unit = {
    if (index < nodes.size - 1) {
      nodes(index).active = false
      index = index + 1
      nodes(index).active = true
      update()
    }
  }

  def previous():Unit = {
    if (index > 0) {
      nodes(index).active = false
      index = index - 1
      nodes(index).active = true
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
    if (nodes.nonEmpty && nodes(index).showFootBox()) footBox else <.div()
  )

  override def makeItSo: PartialFunction[MakeItSo, _] = {
    case s:Sequencer =>
      nodes = s.nodes
      index = s.index
      rerender()
  }
}


class SequenceItem(
  var content:VNode,
  override val key: Option[String] = None,
  val readyForward: () => Boolean = { () => true },
  val enableForward: () => Boolean = { () => true },
  val readyBack: () => Boolean = { () => true },
  val enableBack: () => Boolean = { () => true },
  val showFootBox: () => Boolean = { () => true }
) extends DiffComponent {

  var active:Boolean = false

  override protected def render: DiffNode = <.div(
    ^.cls := "v-slide",
    content
  )

}

object SequenceItem {

  implicit def lift(v:VNode):SequenceItem = new SequenceItem(v)

  implicit def lift(vs:Seq[VNode]):Seq[SequenceItem] = vs.map(v => lift(v))

}
