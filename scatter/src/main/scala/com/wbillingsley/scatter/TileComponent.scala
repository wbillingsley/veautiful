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

  def layoutChildren():Unit = {}

  override def update(): Unit = {
//    layoutChildren()
    super.update()
  }

  override def attach(): Node = {
    logger.trace(s"$uid $this attaching")
    super.attach()
  }

  override def detach(): Unit = {
    logger.trace(s"$uid $this detaching")
    super.detach()
  }


  def size:Option[(Int, Int)] = {
    logger.trace(s"$uid $this size with domNode $domNode")
    domNode.map {
      case e:SVGElement =>
        val b = e.asInstanceOf[js.Dynamic].getBBox()
        (b.width.asInstanceOf[Double].toInt, b.height.asInstanceOf[Double].toInt)
      //case e:HTMLElement => (e.clientWidth, e.clientHeight)
    }
  }

}

object TileComponent {
  val logger:Logger = Logger.getLogger(TileComponent.getClass)
}
