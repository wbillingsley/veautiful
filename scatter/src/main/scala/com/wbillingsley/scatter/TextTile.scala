package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{<, DElement, DiffComponent, DiffNode, SVG, VNode, ^}
import org.scalajs.dom.raw.SVGElement


case class TextTile(tileSpace:TileSpace, text:String) extends Tile(tileSpace) with DiffComponent {


  def boundsPath:VNode = Tile.path(this)

  override def render: DiffNode = {
    println(s"Rendering at size $size")

    <("g", ns = DElement.svgNS)(^.cls := "tile", ^.attr("transform") := s"translate($x, $y);",
      boundsPath,
      SVG.text(^.attr("x") := Tile.contentStart._1, text)
    )
  }

  override def returnType: String = "String"

  override def size: Option[(Int, Int)] = domNode map {
    case n:SVGElement =>
      val r = n.getBoundingClientRect()
      (r.width.toInt, r.height.toInt)
  }


  override def afterAttach(): Unit = {
    super.afterAttach()
    rerender()
  }

  override def setPosition(x: Double, y: Double): Unit = {
    this.x = x.toInt
    this.y = y.toInt
    domNode foreach  { case e:SVGElement =>
      e.setAttribute("transform", s"translate(${x.toInt.toString}, ${y.toInt.toString})")
    }
  }
}

