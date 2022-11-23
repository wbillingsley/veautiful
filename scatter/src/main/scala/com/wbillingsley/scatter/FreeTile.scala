package com.wbillingsley.scatter

import com.wbillingsley.veautiful.OnScreen
import com.wbillingsley.veautiful.html.{VHtmlComponent, ^}
import com.wbillingsley.veautiful.svg.{DSvgElement, SVG}
import org.scalajs.dom.{MouseEvent, SVGElement}

/**
  * When tiles are not in sockets, they can often be moved around the canvas.
  * 
  * To implement this, we define "Free Tile" containers - these serve only to manage the drag events and the rendering
  * order of tiles within a TileSpace.
  * @param tile
  * @tparam T
  */
case class FreeTile[T](tile:Tile[T]) extends VHtmlComponent with OnScreen {
  
  val tileBoundary:DSvgElement = SVG.path(^.cls := "tile-path")
  
  export tile.ts
  export tile.layout
  export tile.emptySockets
  export tile.mobile

  override def setPosition(x: Double, y: Double): Unit = {
    this.x = x.toInt
    this.y = y.toInt
    domNode foreach  { case e:SVGElement =>
      e.setAttribute("transform", s"translate(${x.toInt.toString}, ${y.toInt.toString})")
    }
  }

  // TODO: Make this more efficient
  override def size: Option[(Int, Int)] = domNode map {
    case n:SVGElement =>
      val r = n.getBoundingClientRect()
      (r.width.toInt, r.height.toInt)
  }
  
  override def afterAttach(): Unit = {
    super.afterAttach()
    if (tile.mobile) registerDragListeners()
  }

  val onMouseDown: MouseEvent => Unit = { (e) => 
    e.stopPropagation()
    ts.onMouseDown(this, e)
  }

  val onMouseOver: MouseEvent => Unit = { (e) =>
    e.stopPropagation()
    rerender()
  }

  val onMouseOut: MouseEvent => Unit = { (e) =>
    e.stopPropagation()
    rerender()
  }

  def registerDragListeners():Unit = {
    for { n <- domNode } {
      n.addEventListener("pointerdown", onMouseDown)
      n.addEventListener("pointerout", onMouseOut)
      n.addEventListener("pointerover", onMouseOver)
    }
  }

  override def render = {
    SVG.g(^.cls := "free-tile", ^.attr("transform") := s"translate($x, $y)", tile)
  }

}
