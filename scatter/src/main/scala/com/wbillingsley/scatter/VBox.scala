package com.wbillingsley.scatter

import com.wbillingsley.scatter.TileComponent.logger
import com.wbillingsley.veautiful.html.VHtmlDiffNode
import com.wbillingsley.veautiful.{DiffNode, SVG, ^}


case class VBox[T](children:TileComponent[T]*) extends TileComponent[T] {

  override def render: VHtmlDiffNode = SVG.g(^.attr("transform") := s"translate($x, $y)", children)

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

  override def emptySockets: Seq[(Int, Int, Socket[T])] = {
    for {
      c <- children
      (x, y, s) <- c.emptySockets
    } yield (c.x + x, c.y + y, s)
  }
}