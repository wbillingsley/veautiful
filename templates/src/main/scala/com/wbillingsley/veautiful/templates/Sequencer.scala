package com.wbillingsley.veautiful.templates

import com.wbillingsley.veautiful._
import com.wbillingsley.veautiful.html.{<, Styling, DHtmlComponent, VHtmlDiffNode, DHtmlModifier, VHtmlElement, ^}
import com.wbillingsley.veautiful.reconcilers.Reconciler
import com.wbillingsley.veautiful.templates.Sequencer.{LayoutFunc, defaultFootBoxStyle, sequencerSlideStyle}

object Sequencer {

  type LayoutFunc = (Sequencer, DHtmlModifier, Int) => VHtmlElement

  def footBoxLayout(additionalItems:Seq[VHtmlElement]):LayoutFunc = { (sequencer, item, i) =>
    <.div(^.cls := inheritWrapper.className, item, sequencer.footBoxWithInsert(additionalItems)).build()
  }

  def defaultLayout:LayoutFunc = { (sequencer, item, i) =>
    <.div(^.cls := inheritWrapper.className, item, sequencer.footBox).build()
  }

  def bareLayout:LayoutFunc = { (sequencer, item, i) =>
    <.div(^.cls := inheritWrapper.className, item).build()
  }
  
  /** The surround, that contains the slide deck */
  val sequencerSlideStyle = Styling(
    """position: absolute;
      |top: 0;
      |left: 0;
      |width: 100%;
      |height: 100%;
      |""".stripMargin).modifiedBy(
    ".inactive" -> "visibility: hidden;"
  ).register()
  
  val inheritWrapper = Styling("height: inherit;").register()

  val sequencerGallerySlideStyle = Styling(
    """position: relative;
      |top: 0;
      |left: 0;
      |width: 100%;
      |height: 100%;
      |""".stripMargin).modifiedBy(
  ).register()
  
  
  val defaultFootBoxStyle = Styling(
    """position: absolute;
      |bottom: 10px;
      |right: 10px;
      |""".stripMargin).modifiedBy(
    " button" ->
      """border-color: #6c757d;
        |background-color: #6c757d;
        |border-radius: 0.2rem;
        |color: #fff;
        |""".stripMargin,
    " button:disabled" -> "opacity: 0.7"
  ).register()
  
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
) extends DHtmlComponent with Morphing(SequencerConfig(nodes, index, layout, onIndexChange)) {
  
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

  private def prevButton() =
    if (prop.index > 0) then
      <.button(^.onClick --> previous(), "<")
    else
      <.button(^.attr("disabled") := "disabled", "<")

  private def nextButton() =
    if (prop.index + 1 < nodes.length) then
      <.button(^.onClick --> next(), ">")
    else
      <.button(^.attr("disabled") := "disabled", ">")

  def footBoxWithInsert(additional:Seq[VHtmlElement]) = {
    <.div(^.cls := s"v-sequencer-footbox ${defaultFootBoxStyle.className} ",
      additional,
      prevButton(),
      <.span(s" ${prop.index+1} / ${nodes.size} "),
      nextButton()
    )
  }
  
  def footBox = {
    <.div(^.cls := s"v-sequencer-footbox ${defaultFootBoxStyle.className} ",
      prevButton(),
      <.span(s" ${prop.index+1} / ${nodes.size} "),
      nextButton()
    )
  }
  
  private def layoutItem(item:SequenceItem, i:Int):VHtmlElement = {
    item match {
      case csi:CustomSequenceItem => csi.layout(this, i)
      case n:VHtmlElement => layout(this, n, i)
    }
  }

  override def render = <.div(^.cls := "v-sequencer",
    <.div(^.cls := "v-sequencer-inner",
      for {
        (item, i) <- prop.nodes.zipWithIndex
      } yield <.div(^.reconciler := Reconciler.onlyIf(prop.index == i),
        ^.cls := (if (prop.index == i) s"${sequencerSlideStyle.className} v-sequencer-slide active" else s"${sequencerSlideStyle.className} v-sequencer-slide inactive"),
        layoutItem(item, i)
      )
    )
  )

  override def beforeAttach(): Unit = {
    super.beforeAttach()
    
    // Ensure the template styles are installed in the page
    templateStyleSuite.install()
  }

}

type SequenceItem = VHtmlElement // | CustomSequenceItem

trait CustomSequenceItem {
  def layout(sequencer:Sequencer, index:Int):VHtmlElement
}

