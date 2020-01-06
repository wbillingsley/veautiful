package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful._
import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^}

object Sequencer {

  type LayoutFunc = (Sequencer, SequenceItem, Int) => VHtmlNode

  def page = {
    VSlides(width=1024, height=680)(Seq(
      <.div("Page One"),
      <.div("Page Two")
    ))
  }


  def defaultLayout:LayoutFunc = { case (sequencer, s, _) =>
    <.div(
      ^.cls := "v-slide",
      s.content,
      sequencer.footBox
    )
  }

}

case class Sequencer(override val key:Option[String] = None, layout:Sequencer.LayoutFunc = Sequencer.defaultLayout)(var nodes: Seq[SequenceItem], var index:Int = 0) extends VHtmlComponent with MakeItSo {

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

  def footBox:VHtmlNode = {
    <.div(^.cls := "v-sequencer-footbox",
      <.button(^.onClick --> previous, "<"),
      <.span(s" ${index+1} / ${nodes.size} "),
      <.button(^.onClick --> next, ">")
    )
  }

  override def render: VHtmlDiffNode = <.div(^.cls := "v-sequencer",
    <.div(^.cls := "v-sequencer-inner",
      for {
        (n, i) <- nodes.zipWithIndex
      } yield <.div(
        ^.cls := (if (index == i) "v-sequencer-slide active" else "v-sequencer-slide inactive"),
        layout(this, n, i)
      )
    )
  )

  override def makeItSo: PartialFunction[MakeItSo, _] = {
    case s:Sequencer =>
      nodes = s.nodes
      index = s.index
      rerender()
  }
}


class SequenceItem(
  var content:VHtmlNode,
  val readyForward: () => Boolean = { () => true },
  val enableForward: () => Boolean = { () => true },
  val readyBack: () => Boolean = { () => true },
  val enableBack: () => Boolean = { () => true },
  val showFootBox: () => Boolean = { () => true }
) {

  var active:Boolean = false

}

object SequenceItem {

  implicit def lift(v:VHtmlNode):SequenceItem = new SequenceItem(v)

  implicit def lift(vs:Seq[VHtmlNode]):Seq[SequenceItem] = vs.map(v => lift(v))

}
