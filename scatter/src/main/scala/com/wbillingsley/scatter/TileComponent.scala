package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{DiffComponent, VNode}
import org.scalajs.dom.raw.{HTMLElement, SVGElement}

import scala.scalajs.js

/**
  * A tile component is a component that can be included within a tile.
  */
trait TileComponent extends DiffComponent {

  var x = 0

  var y = 0

  def layoutChildren():Unit = {}

  def size:Option[(Int, Int)] = {
    domNode.map {
      case e:SVGElement =>
        val b = e.asInstanceOf[js.Dynamic].getBBox()
        (b.width.asInstanceOf[Double].toInt, b.height.asInstanceOf[Double].toInt)
      case e:HTMLElement => (e.clientWidth, e.clientHeight)
    }
  }

}
