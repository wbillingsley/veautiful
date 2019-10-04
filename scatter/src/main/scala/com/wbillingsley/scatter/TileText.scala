package com.wbillingsley.scatter
import com.wbillingsley.veautiful.{DiffNode, SVG, VNode, ^}
import org.scalajs.dom.raw.SVGElement

case class TileText(text:String) extends TileComponent {

  override def render: DiffNode = {
    println(s"Render called on $this $lastRendered $domNode")

    SVG.text(^.attr("x") := x, ^.attr("y") := y, ^.cls := "tile-text", text)
  }


  override def size: Option[(Int, Int)] = {
    println(s"Size called on $this $lastRendered $domNode")
    super.size
  }
}
