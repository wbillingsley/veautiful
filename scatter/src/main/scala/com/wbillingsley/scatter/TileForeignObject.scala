package com.wbillingsley.scatter

import com.wbillingsley.veautiful.html.{SVG, VHtmlDiffNode, VHtmlNode, ^}

case class TileForeignObject[T](content: VHtmlNode) extends TileComponent[T] {

  override def emptySockets: Seq[(Int, Int, Socket[T])] = Seq.empty

  var width:Int = 20
  var height:Int = 20

  override protected def render: VHtmlDiffNode =
    SVG.foreignObject(^.attr("width") := width, ^.attr("height") := height,
      content
    )


  override def size:Option[(Int, Int)] = {
    // We need the size of the contained object
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