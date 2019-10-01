package com.wbillingsley.scatter

import com.wbillingsley.veautiful.{<, DiffComponent, DiffNode, Layout, OnScreen, ^}
import org.scalajs.dom.raw.{MouseEvent, SVGElement}

import scala.collection.mutable

case class TileSpace(override val key:Option[String] = None)(val prefSize:(Int, Int) = (480, 640)) extends DiffComponent with OnScreen {

  val tiles:mutable.SortedSet[Tile] = mutable.SortedSet.empty(Ordering.by(_.hashCode()))

  override def render: DiffNode = <.svg(^.attr("width") := prefSize._1.toString, ^.attr("height") := prefSize._2.toString, ^.cls := "scatter-area",
    tiles.toSeq
  )

  override def setPosition(x: Double, y: Double): Unit = ???

  override def size: Option[(Int, Int)] = Some(prefSize)

  var dragging:Option[DragInfo] = None

  def startDragging(item:Tile, x:Double, y:Double):Unit = {
    dragging = Some(DragInfo(item, item.x, item.y, x, y))
  }

  def onMouseDrag(e:MouseEvent):Unit = {
    for {
      DragInfo(tile, ix, iy, mx, my) <- dragging
    } {
      e.preventDefault()
      val x = e.clientX
      val y = e.clientY
      tile.setPosition(ix + x - mx, iy + y - my)
    }
  }

  def onMouseUp(e:MouseEvent):Unit = {
    dragging = None
  }

  def registerDragListeners():Unit = {
    for { n <- domNode } {
      n.addEventListener("mousemove", onMouseDrag)
      n.addEventListener("mouseup", onMouseUp)
      n.addEventListener("mouseleave", onMouseUp)
    }
  }

  override def afterAttach(): Unit = {
    super.afterAttach()
    registerDragListeners()
  }

  /**
    * If this TileSpace is attached (and in the page), determines any scale that has been applied to it through CSS.
    */
  def scale = {
    domNode map { case e:SVGElement =>
      e.getBoundingClientRect().width / e.clientWidth
    }
  }
}
