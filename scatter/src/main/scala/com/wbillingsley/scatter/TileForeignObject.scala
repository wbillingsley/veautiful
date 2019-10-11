package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{<, DiffNode, SVG, VNode, ^}

case class TileForeignObject[T](content: VNode) extends TileComponent[T] {

  override def emptySockets: Seq[(Int, Int, Socket[T])] = Seq.empty

  var width:Int = 20
  var height:Int = 20

  override protected def render: DiffNode =
    SVG.foreignObject(^.attr("width") := width, ^.attr("height") := height,
      content
    )


  override def size:Option[(Int, Int)] = {
    for {
      e <- domNode
      c <- {
        if (e.childElementCount > 0) Some(e.children(0)) else None
      }
    } yield TileComponent.sizeOf(c)
  }

  override def layoutChildren(): Unit = {
    super.layoutChildren()
    for { (w, h) <- size } {
      width = w
      height = h
      rerender()
    }
  }


}