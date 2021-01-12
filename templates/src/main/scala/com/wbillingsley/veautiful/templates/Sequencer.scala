package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful._
import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, VHtmlNode, ^}
import com.wbillingsley.veautiful.reconcilers.Reconciler
import com.wbillingsley.veautiful.templates.Sequencer.LayoutFunc

object Sequencer {

  type LayoutFunc = (Sequencer, VHtmlNode, Int) => VHtmlNode

  def defaultLayout:LayoutFunc = { (sequencer, item, i) =>
    <.div(item, sequencer.footBox)
  }

}

case class SequencerConfig(
  nodes: Seq[SequenceItem], 
  index:Int = 0, 
  layout:Sequencer.LayoutFunc = Sequencer.defaultLayout,
  onIndexChange: Option[Int => Unit] = None
)

/**
  * A Sequencer renders a series of nodes into the page, styling one of them to be active.
  * If, in stead, you only want the active element to be present in the dom, just render nodes(i).
  *
  * @param key
  * @param layout
  * @param nodes
  * @param index
  */
case class Sequencer(override val key:Option[String] = None)(
  nodes: Seq[SequenceItem], index:Int = 0, layout:Sequencer.LayoutFunc = Sequencer.defaultLayout,
  onIndexChange: Option[Int => Unit] = None
) extends VHtmlComponent with Morphing(SequencerConfig(nodes, index, layout, onIndexChange)) {
  
  val morpher = createMorpher(this)

  def next():Unit = {
    if (prop.index < nodes.size - 1) {
      prop.onIndexChange match {
        case Some(f) => f(prop.index + 1)
        case None => updateProp(prop.copy(index = prop.index + 1)); rerender()
      }
    }
  }

  def previous():Unit = {
    if (prop.index > 0) {
      prop.onIndexChange match {
        case Some(f) => f(prop.index - 1)
        case None => updateProp(prop.copy(index = prop.index - 1)); rerender()
      }
    }
  }

  def footBox:VHtmlNode = {
    <.div(^.cls := "v-sequencer-footbox",
      <.button(^.onClick --> previous(), "<"),
      <.span(s" ${prop.index+1} / ${nodes.size} "),
      <.button(^.onClick --> next(), ">")
    )
  }
  
  private def layoutItem(item:SequenceItem, i:Int):VHtmlNode = {
    item match {
      case csi:CustomSequenceItem => csi.layout(this, i)
      case n:VHtmlNode => layout(this, n, i)
    }
  }

  override def render: VHtmlDiffNode = <.div(^.cls := "v-sequencer",
    <.div(^.cls := "v-sequencer-inner",
      for {
        (item, i) <- prop.nodes.zipWithIndex
      } yield <.div(^.reconciler := Reconciler.onlyIf(prop.index == i),
        ^.cls := (if (prop.index == i) "v-sequencer-slide active" else "v-sequencer-slide inactive"),
        layoutItem(item, i)
      )
    )
  )

}

type SequenceItem = VHtmlNode | CustomSequenceItem

trait CustomSequenceItem {
  def layout(sequencer:Sequencer, index:Int):VHtmlNode
}

