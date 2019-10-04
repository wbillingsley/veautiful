package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{<, DElement, DiffComponent, DiffNode, Layout, OnScreen, SVG, Update, VNode, ^}
import org.scalajs.dom.raw.{HTMLElement, MouseEvent, SVGElement}

abstract class Tile(val ts:TileSpace) extends OnScreen with DiffComponent {

  def free:Boolean = within.isEmpty

  var within:Option[Socket] = None

  def sockets:Seq[Socket] = Seq.empty

  def returnType:String

  def onMouseDown(e:MouseEvent):Unit = {
    ts.startDragging(this, e.clientX, e.clientY)
  }

  def registerDragListeners():Unit = {
    for { n <- domNode } {
      n.addEventListener("pointerdown", onMouseDown)
    }
  }

  override def render: DiffNode = {
    val c = tileContent

    <("g", ns = DElement.svgNS)(^.cls := "tile", ^.attr("transform") := s"translate($x, $y)",
      Tile.path(c),
      SVG.g(^.attr("transform") := s"translate(${Tile.boxStartX + Tile.padding}, ${Tile.padding})", c)
    )
  }

  def tileContent:TileComponent

  override def afterAttach(): Unit = {
    super.afterAttach()
    registerDragListeners()
  }

  override def size: Option[(Int, Int)] = domNode map {
    case n:SVGElement =>
      val r = n.getBoundingClientRect()
      (r.width.toInt, r.height.toInt)
  }

  override def setPosition(x: Double, y: Double): Unit = {
    this.x = x.toInt
    this.y = y.toInt
    domNode foreach  { case e:SVGElement =>
      e.setAttribute("transform", s"translate(${x.toInt.toString}, ${y.toInt.toString})")
    }
  }

  def layout() = {
    tileContent.layoutChildren()
  }

}

object Tile {

  def path(tc:TileComponent):VNode = {
    val (w, h) = tc.size getOrElse (20, 20)
    <("path", ns=DElement.svgNS)(^.attr("d") := boxAndArc(w, h))
  }

  def corner:String = "M 9 3 l 0 -3 "

  val padding = 3

  val typeLoopRadius = 9

  val typeLoopY1 = 15

  val typeLoopY2 = 4

  val boxStartX = 14

  val contentStart:(Int, Int) = (16, 2)

  val fontSize:Int = 15

  val typeCorner:String = s"M $boxStartX $typeLoopY1 A $typeLoopRadius $typeLoopRadius 0 1 1 $boxStartX $typeLoopY2 L $boxStartX 0 "

  def boxAndArc(w:Int, h:Int):String = s"$typeCorner l $w 0 l 0 $h l ${-w} 0 z"

}




