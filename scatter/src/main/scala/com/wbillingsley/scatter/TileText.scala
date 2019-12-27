package com.wbillingsley.scatter
import com.wbillingsley.veautiful.html.{SVG, VHtmlDiffNode, ^}

case class TileText[T](text:String) extends TileComponent[T] {

  override def render: VHtmlDiffNode = {
    SVG.text(^.attr("x") := x, ^.attr("y") := y, ^.cls := "tile-text", text)
  }

  override def emptySockets: Seq[(Int, Int, Socket[T])] = Seq.empty
}
