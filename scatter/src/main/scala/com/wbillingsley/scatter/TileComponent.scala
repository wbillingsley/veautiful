package com.wbillingsley.scatter

import com.wbillingsley.veautiful.html.VHtmlComponent
import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.{DiffComponent, DiffNode, VNode}
import org.scalajs.dom.{Element, Node}
import org.scalajs.dom.raw.{HTMLElement, SVGElement}

import scala.scalajs.js
import scala.util.Random

/**
  * A tile component is a component that can be included within a tile.
  */
trait TileComponent[T] extends VHtmlComponent {

  import TileComponent.logger

  val uid = Random.nextString(6)

  var x = 0

  var y = 0

  def repositionNode():Unit = rerender()

  def layoutChildren():Unit = {
    logger.trace(s"layoutChildren: $this")
  }

  def emptySockets:Seq[(Int, Int, Socket[T])]

  override def attach(): Element = {
    logger.trace(s"Attach: $this $uid")
    super.attach()
  }

  override def detach(): Unit = {
    logger.trace(s"Detach: $this $uid")
    super.detach()
  }

  def size:Option[(Int, Int)]

  /**
    * Measures the size of the DOM element. Note that this can cause the browser to have to do a layout pass.
    * @return
    */
  def measuredSize:Option[(Int, Int)] = {
    logger.trace(s"Size: $this $uid domNode $domNode")
    domNode.map(TileComponent.sizeOf)
  }

}

object TileComponent {
  val logger:Logger = Logger.getLogger(TileComponent.getClass)

  def sizeOf(e:Element):(Int, Int) = e match {
    case svg:SVGElement =>
      val b = e.asInstanceOf[js.Dynamic].getBBox()
      val v = (b.width.asInstanceOf[Double].toInt, b.height.asInstanceOf[Double].toInt)
      logger.trace(s"Size was $v")
      v

    case h:HTMLElement =>
      (h.offsetWidth.toInt, h.offsetHeight.toInt)
  }
}
