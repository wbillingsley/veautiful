package com.wbillingsley.scatter
import com.wbillingsley.veautiful.{DiffNode, SVG, ^}

case class TileText(text:String) extends TileComponent {

  override def render: DiffNode = {
    SVG.text(^.attr("x") := x, ^.attr("y") := y, ^.cls := "tile-text", text)
  }

  override def emptySockets: Seq[(Int, Int, Socket)] = Seq.empty
}
