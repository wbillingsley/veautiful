package com.wbillingsley.scatter

import com.wbillingsley.veautiful.html.{SVG, VHtmlDiffNode, ^}
import com.wbillingsley.veautiful.logging.Logger

case class HBox[T](children:TileComponent[T]*) extends TileComponent[T] {

  override def render = SVG.g(^.attr("transform") := s"translate($x, $y)", children)

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

  override def emptySockets: Seq[(Int, Int, Socket[T])] = {
    for {
      c <- children
      (x, y, s) <- c.emptySockets
    } yield (c.x + x, c.y + y, s)
  }

  override def size: Option[(Int, Int)] = {
    var maxH:Int = 0
    var totalW:Int = 0
    
    for {
      c <- children
      (w, h) <- c.size
    } do {
      maxH = Math.max(h, maxH)
      totalW += w + HBox.padding
    }
    
    Some(totalW, maxH)
  } 
}

object HBox {
  val logger:Logger = Logger.getLogger(HBox.getClass)

  val padding = 3

  val tabDist = 10;
}