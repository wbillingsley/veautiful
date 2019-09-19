package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{<, DElement, DiffComponent, DiffNode, VNode, ^}
import org.scalajs.dom.raw.SVGElement


case class TextTile(text:String) extends Tile with DiffComponent {


  var bounds: (Int, Int, Int, Int) = {
    val (w, h) = prefSize
    (0, 0, w, h)
  }

  val boundsPath:VNode = Tile.path(this)

  override val render: DiffNode = <("g", ns=DElement.svgNS)(^.cls := "tile", ^.attr("transform") := s"translate(${bounds._1}px, ${bounds._2}px);",
    boundsPath,
    <("text", ns=DElement.svgNS)(text)
  )

  override def returnType: String = "String"


  override def prefSize: (Int, Int) = domNode match {
    case Some(n:SVGElement) =>
      val r = n.getBoundingClientRect()
      (r.width.toInt, r.height.toInt)
    case None => (20, 20)
  }

  override def layout(bounds: (Int, Int, Int, Int)): Unit = {
    this.bounds = bounds
  }
}

