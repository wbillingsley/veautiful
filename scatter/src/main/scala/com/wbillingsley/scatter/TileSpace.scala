package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{<, DiffComponent, DiffNode, Layout, OnScreen, ^}
import org.scalajs.dom.raw.{MouseEvent, SVGElement}

import scala.collection.mutable

case class TileSpace(override val key:Option[String] = None)(val prefSize:(Int, Int) = (480, 640)) extends DiffComponent {

  val tiles:mutable.Buffer[Tile] = mutable.Buffer.empty

  override def render: DiffNode = <.svg(^.attr("width") := prefSize._1.toString, ^.attr("height") := prefSize._2.toString, ^.cls := "scatter-area",
    tiles.toSeq
  )

  var dragging:Option[DragInfo] = None

  var activeSocket:Option[Socket] = None

  def startDragging(item:Tile, x:Double, y:Double):Unit = {
    dragging = Some(DragInfo(item, item.x, item.y, x, y))
  }

  def onMouseDown(t:Tile, e:MouseEvent):Unit = {
    if (tiles.contains(t)) {
      bringToFront(t);
      layout()
      startDragging(t, e.clientX, e.clientY)
    }
  }

  def onMouseDrag(e:MouseEvent):Unit = {
    for {
      DragInfo(tile, ix, iy, mx, my) <- dragging
    } {
      e.preventDefault()
      val x = e.clientX
      val y = e.clientY
      val newTx = ix + x - mx
      val newTy = iy + y - my
      tile.setPosition(newTx, newTy)

      def dist(a:Double, b:Double) = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2))

      val closestSockets = tiles.flatMap(_.emptySockets)
        .filter({
          case (_, _, s) => s.within != tile
        })
        .map({
          case (sx, sy, s) =>
            val d = dist(s.within.x + sx - newTx, s.within.y + sy - newTy)
            d -> s
        })
        .filter(_._1 < 50)

      activeSocket = if (closestSockets.isEmpty) None else Some(closestSockets.minBy(_._1)._2)
      rerender()
    }
  }

  def onMouseUp(e:MouseEvent):Unit = {
    for {
      s <- activeSocket
      DragInfo(t:Tile, _, _, _, _) <- dragging
    } dropIntoSocket(t, s)

    dragging = None
    activeSocket = None
    rerender()
  }

  def activateSocket(x:Int, y:Int) = {
    val s = tiles.flatMap(_.emptySockets)
  }

  private def dropIntoSocket(t:Tile, s:Socket):Unit = {
    println("Drop!")

    s.content = Some(t)
    t.setPosition(0,0)
    tiles.remove(tiles.indexOf(t))
    layout()
  }

  def registerDragListeners():Unit = {
    for { n <- domNode } {
      n.addEventListener("pointermove", onMouseDrag)
      n.addEventListener("pointerup", onMouseUp)
      n.addEventListener("pointerleave", onMouseUp)
    }
  }

  override def afterAttach(): Unit = {
    super.afterAttach()
    registerDragListeners()
    layout()
  }

  def bringToFront(t:Tile):Unit = {
    val newOrder = tiles.filter(_ != t)
    newOrder.append(t)
    tiles.clear
    tiles.appendAll(newOrder)
    rerender()
  }

  /**
    * If this TileSpace is attached (and in the page), determines any scale that has been applied to it through CSS.
    */
  def scale = {
    domNode map { case e:SVGElement =>
      e.getBoundingClientRect().width / e.clientWidth
    }
  }

  def layout(): Unit = {
    for { t <- tiles } t.layout()
  }
}
