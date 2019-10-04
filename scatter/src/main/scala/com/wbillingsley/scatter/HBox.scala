package com.wbillingsley.scatter

import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.{DiffNode, SVG, ^}
import org.scalajs.dom.Node

case class HBox(children:TileComponent*) extends TileComponent {

  override def render: DiffNode = SVG.g(^.attr("transform") := s"translate($x, $y)", children)

  override def layoutChildren():Unit = {
    super.layoutChildren()
    children.foreach(_.layoutChildren())

    var x = 0
    val y = 0
    for { (c, i) <- children.iterator.zipWithIndex } {
      c.x = x
      c.repositionNode()
//      println(s"Item $i $c size is ${c.size}")
      x = x + c.size.map(_._1).getOrElse(0) + HBox.padding
    }
  }
}

object HBox {
  val logger:Logger = Logger.getLogger(HBox.getClass)

  val padding = 3

  val tabDist = 10;
}