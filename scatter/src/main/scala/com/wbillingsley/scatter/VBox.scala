package com.wbillingsley.scatter

import com.wbillingsley.veautiful.html.{SVG, VHtmlDiffNode, ^}


case class VBox[T](children:TileComponent[T]*) extends TileComponent[T] {

  override def render = SVG.g(^.attr("transform") := s"translate($x, $y)", children)

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

  override def size: Option[(Int, Int)] = {
    var maxW:Int = 0
    var totalH:Int = 0

    for {
      c <- children
      (w, h) <- c.size
    } do {
      maxW = Math.max(w, maxW)
      totalH += h + HBox.padding
    }

    Some(maxW, totalH)
  }
}