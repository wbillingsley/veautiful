package com.wbillingsley.scatter

import com.wbillingsley.scatter.TileComponent.logger
import com.wbillingsley.veautiful.{DiffNode, SVG, ^}


case class VBox(children:TileComponent*) extends TileComponent {

  override def render: DiffNode = SVG.g(^.attr("transform") := s"translate($x, $y)", children)

  override def layoutChildren():Unit = {
    super.layoutChildren()
    children.foreach(_.layoutChildren())

    val x = 0
    var y = 0
    for { (c, i) <- children.iterator.zipWithIndex } {
      c.y = y
      c.repositionNode()
      //      println(s"Item $i $c size is ${c.size}")
      y = y + c.size.map(_._2).getOrElse(0) + HBox.padding
    }
  }

  override def emptySockets: Seq[(Int, Int, Socket)] = {
    for {
      c <- children
      (x, y, s) <- c.emptySockets
    } yield (c.x + x, c.y + y, s)
  }
}