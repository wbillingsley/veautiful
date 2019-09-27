package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{<, DElement, DiffComponent, DiffNode, Layout, OnScreen, Update, VNode, ^}
import org.scalajs.dom.raw.{HTMLElement, MouseEvent, SVGElement}

case class DragLocation(itemStartX:Double, itemStartY:Double, mouseStartX: Double, mouseStartY:Double)

trait Tile extends OnScreen with VNode {

  var within:Option[Socket] = None

  def sockets:Seq[Socket] = Seq.empty

  def returnType:String

  var mouseDownLoc:Option[DragLocation] = None

  def onMouseDown(e:MouseEvent):Unit = {
    println(e)
    mouseDownLoc = Some(DragLocation(x, y, e.clientX, e.clientY))
  }

  def onMouseDrag(e:MouseEvent):Unit = {
    for {
      DragLocation(ix, iy, mx, my) <- mouseDownLoc
    } {
      val x = e.clientX
      val y = e.clientY
      setPosition(ix + x - mx, iy + y - my)
    }
  }

  def onMouseUp(e:MouseEvent):Unit = {
    mouseDownLoc = None
  }

  def registerDragListeners():Unit = {
    for { n <- domNode } {
      n.addEventListener("mousedown", onMouseDown)
      n.addEventListener("mousemove", onMouseDrag)
      n.addEventListener("mouseup", onMouseUp)
    }
  }

  override def afterAttach(): Unit = {
    super.afterAttach()
    registerDragListeners()
  }


}

trait Socket {

}

object Tile {

  def path(tile:Tile):VNode = {
    val (x, y, w, h) = tile.bounds getOrElse (0, 0, 20, 20)

    <("path", ns=DElement.svgNS)(^.attr("d") := boxAndArc(w, h))
  }

  def corner:String = "M 9 3 l 0 -3 "

  val typeLoopRadius = 9

  val typeLoopY1 = 15

  val typeLoopY2 = 4

  val boxStartX = 14

  val contentStart:(Int, Int) = (16, 2)

  val fontSize:Int = 15

  val typeCorner:String = s"M $boxStartX $typeLoopY1 A $typeLoopRadius $typeLoopRadius 0 1 1 $boxStartX $typeLoopY2 L $boxStartX 0 "

  def boxAndArc(w:Int, h:Int):String = s"$typeCorner l $w 0 l 0 $h l ${-w} 0 z"

}




