package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful._
import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^}
import com.wbillingsley.veautiful.templates.Sequencer.LayoutFunc

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
      s.content,
      sequencer.footBox
    )
  }

}

/**
  * A Sequencer renders a series of nodes into the page, styling one of them to be active.
  * If, in stead, you only want the active element to be present in the dom, just render nodes(i).
  *
  * @param key
  * @param layout
  * @param nodes
  * @param index
  */
case class Sequencer(override val key:Option[String] = None)(var nodes: Seq[SequenceItem], var index:Int = 0, var layout:Sequencer.LayoutFunc = Sequencer.defaultLayout) extends VHtmlComponent with MakeItSo {

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
        n.layoutOverride match {
          case Some(lo) => lo(this, n, i)
          case _ => layout(this, n, i)
        }
      )
    )
  )

  override def makeItSo: PartialFunction[MakeItSo, _] = {
    case s:Sequencer =>
      nodes = s.nodes
      index = s.index
      layout = s.layout
      rerender()
  }
}


class SequenceItem(
  var content:VHtmlNode,
  val layoutOverride: Option[LayoutFunc] = None
) {
  var active:Boolean = false
}

object SequenceItem {

  implicit def lift(v:VHtmlNode):SequenceItem = new SequenceItem(v)

  implicit def lift(vs:Seq[VHtmlNode]):Seq[SequenceItem] = vs.map(v => lift(v))

}
