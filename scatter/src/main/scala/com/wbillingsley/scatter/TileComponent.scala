package com.wbillingsley.scatter

import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.{DiffComponent, VNode}
import org.scalajs.dom.Node
import org.scalajs.dom.raw.{HTMLElement, SVGElement}

import scala.scalajs.js
import scala.util.Random

/**
  * A tile component is a component that can be included within a tile.
  */
trait TileComponent extends DiffComponent {

  import TileComponent.logger

  val uid = Random.nextString(6)

  var x = 0

  var y = 0

  def repositionNode():Unit = rerender()

  def layoutChildren():Unit = {
    logger.trace(s"layoutChildren: $this")
  }

  def emptySockets:Seq[(Int, Int, Socket)]

  override def attach(): Node = {
    logger.trace(s"Attach: $this $uid")
    super.attach()
  }

  override def detach(): Unit = {
    logger.trace(s"Detach: $this $uid")
    super.detach()
  }


  def size:Option[(Int, Int)] = {
    logger.trace(s"Size: $this $uid domNode $domNode")
    domNode.map {
      case e:SVGElement =>
        val b = e.asInstanceOf[js.Dynamic].getBBox()
        val v = (b.width.asInstanceOf[Double].toInt, b.height.asInstanceOf[Double].toInt)
        logger.trace(s"Size was $v")
        v
      //case e:HTMLElement => (e.clientWidth, e.clientHeight)
    }
  }

}

object TileComponent {
  val logger:Logger = Logger.getLogger(TileComponent.getClass)
}
