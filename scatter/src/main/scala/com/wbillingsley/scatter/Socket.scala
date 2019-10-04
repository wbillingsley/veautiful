package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{<, DElement, DiffComponent, DiffNode, OnScreen, ^}
import org.scalajs.dom.raw.SVGElement

class Socket(within:Tile) extends TileComponent {

  var content:Option[Tile] = None

  override def render: DiffNode = {
    <("g", ns = DElement.svgNS)(^.cls := "socket", ^.attr("transform") := s"translate($x, $y)",
      Tile.path(this)
    )
  }

  /*
  override def size: Option[(Int, Int)] = domNode map {
    case n:SVGElement =>
      val r = n.getBoundingClientRect()
      (r.width.toInt, r.height.toInt)
  }*/

}