package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{DiffNode, SVG, ^}

case class HBox(children:TileComponent*) extends TileComponent {

  override def render: DiffNode = SVG.g(^.attr("transform") := s"translate($x, $y)", children)

  override def layoutChildren():Unit = {
    var x = 0
    val y = 0
    for { (c, i) <- children.iterator.zipWithIndex } {
      c.x = x
      println(s"Item $i $c size is ${c.size}")
      x = x + c.size.map(_._1).getOrElse(0)
    }
  }
}