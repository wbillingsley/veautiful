package com.wbillingsley.scatter

import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.{<, DiffComponent, DiffNode, Layout, OnScreen, ^}
import org.scalajs.dom
import org.scalajs.dom.raw.{Element, MouseEvent, SVGElement}

import scala.collection.mutable

case class TileSpace(override val key:Option[String] = None, val language:TileLanguage)(val prefSize:(Int, Int) = (480, 640)) extends DiffComponent {

  import TileSpace._

  val tiles:mutable.Buffer[Tile] = mutable.Buffer.empty

  override def render: DiffNode = <.svg(^.attr("width") := prefSize._1.toString, ^.attr("height") := prefSize._2.toString, ^.cls := "scatter-area",
    tiles.toSeq
  )

  var dragging:Option[DragInfo[Tile]] = None

  /**
    * The socket that should be highlighted as a target for drop events
    */
  var activeSocket:Option[Socket] = None

  /**
    * The tile that should be highlighted as a target for pop events
    */
  var activeTile:Option[Tile] = None

  case class PopTimeOut(t:Tile, s:Socket, x:Int, y:Int, cx:Int, cy:Int, timeOutId:Int)

  var popTimeOut:Option[PopTimeOut] = None

  def setPopTimeOut(t:Tile, s:Socket, x:Int, y:Int, cx:Int, cy:Int):Unit = {
    logger.trace("Starting pop timeout")

    val id = dom.window.setTimeout(
      () => {
        pullFromSocket(t, s, x, y)
        rerender()
        layout()
        startDragging(t, cx, cy)
      },
      500
    )

    popTimeOut = Some(PopTimeOut(t, s, x, y, cx, cy, id))
  }

  def resetPopTimeOut(x:Int, y:Int, cx:Int, cy:Int):Unit = {
    logger.trace("Resetting pop timeout")
    for { p <- popTimeOut } {
      dom.window.clearTimeout(p.timeOutId)
      setPopTimeOut(p.t, p.s, x, y, cx, cy)
    }
  }

  def cancelPopTimeOut():Unit = {
    logger.trace("Cancelling pop timeout")

    for {
      p <- popTimeOut
    } dom.window.clearTimeout(p.timeOutId)

    popTimeOut = None
  }

  def startDragging(item:Tile, x:Double, y:Double):Unit = {
    dragging = Some(DragInfo(item, item.x, item.y, x, y))
  }

  /**
    * Calculates the relative location of a tile or tile component that is attached and displayed to this tileSpace
    * @param tc the tileComponent whose co-ordinates should be calculated
    * @return
    */
  def relativeLocation(tc:DiffComponent):(Int, Int) = {
    val s = scale getOrElse 1.0
    val (tx, ty) = screenLocation(tc)
    val (mx, my) = screenLocation(this)
    (((tx - mx) / s).toInt, ((ty - my) / s).toInt)
  }

  def onMouseDown(t:Tile, e:MouseEvent):Unit = {
    e.preventDefault()

    def readyDrag(ft:Tile):Unit = {
      if (ft.mobile && tiles.contains(ft)) {
        bringToFront(ft);
        layout()
        startDragging(ft, e.clientX, e.clientY)
      }
    }

    t.within match {
      case Some(s) =>
        val (x, y) = relativeLocation(t)
        setPopTimeOut(t, s, x, y, e.clientX.toInt, e.clientY.toInt)
        readyDrag(s.freeParent)

      case None =>
        readyDrag(t)
    }
  }

  def onMouseDrag(e:MouseEvent):Unit = {
    for {
      DragInfo(tile, ix, iy, mx, my) <- dragging
    } {
      e.preventDefault()
      val (tx, ty) = relativeLocation(tile)
      resetPopTimeOut(tx, ty, e.clientX.toInt, e.clientY.toInt)

      val x = e.clientX
      val y = e.clientY
      val s = scale getOrElse 1.0
      val newTx = ix + (x - mx) / s
      val newTy = iy + (y - my) / s
      tile.setPosition(newTx, newTy)

      def dist(a:Double, b:Double) = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2))

      val allSockets = tiles.flatMap(_.emptySockets)

      val closestSockets = allSockets
        .filter({
          case (_, _, s) => s.freeParent != tile
        })
        .map({
          case (sx, sy, s) =>
            val t = s.freeParent
            val d = dist(t.x + sx - newTx, t.y + sy - newTy)
            d -> s
        })
        .filter(_._1 < 50)

      activeSocket = if (closestSockets.isEmpty) None else Some(closestSockets.minBy(_._1)._2)
      rerender()
    }
  }

  def onMouseUp(e:MouseEvent):Unit = {
    cancelPopTimeOut()

    for {
      s <- activeSocket
      DragInfo(t:Tile, _, _, _, _) <- dragging
    } dropIntoSocket(t, s)

    dragging = None
    activeSocket = None
    rerender()
  }

  private def dropIntoSocket(t:Tile, s:Socket):Unit = {
    TileSpace.logger.debug(s"Dropped $t into $s")

    s.onFilledWith(t)
    t.onPlacedInSocket(s)
    tiles.remove(tiles.indexOf(t))
    layout()
  }

  private def pullFromSocket(t:Tile, s:Socket, x:Int, y:Int):Unit = {
    s.onRemoved(t)
    t.onRemovedFromSocket(s, x, y)
    tiles.append(t)
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

object TileSpace {

  val logger:Logger = Logger.getLogger(TileSpace.getClass)

  def screenLocation(n:DiffComponent):(Int, Int) = {
    n.domNode.map { e =>
      val r = e.getBoundingClientRect()
      (r.left.toInt, r.top.toInt)
    } getOrElse (0, 0)
  }

}
