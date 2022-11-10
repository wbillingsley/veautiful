package com.wbillingsley.scatter

import com.wbillingsley.veautiful.html.{<, VHtmlComponent, VHtmlDiffNode, ^}
import com.wbillingsley.veautiful.logging.Logger
import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, SVGElement}

import scala.collection.mutable
import scala.util.Random

case class TileSpace[T](override val key:Option[String] = None, val language:TileLanguage[T])(val canvasSize:(Int, Int) = (480, 640)) extends VHtmlComponent {

  import TileSpace._

  /** A tile space contains some set of free tiles */
  private val freeTiles:mutable.Buffer[FreeTile[T]] = mutable.Buffer.empty

  override def render: VHtmlDiffNode = <.svg(^.attr("width") := canvasSize._1.toString, ^.attr("height") := canvasSize._2.toString, ^.cls := "scatter-area",
    freeTiles
  )

  var dragging:Option[DragInfo[FreeTile[T]]] = None

  /**
    * The socket that should be highlighted as a target for drop events
    */
  var activeSocket:Option[Socket[T]] = None

  /**
    * The tile that should be highlighted as a target for pop events
    */
  var activeTile:Option[Tile[T]] = None

  /** Used to track when a mousedown on a tile should pop the tile out of the socket */
  case class PopTimeOut(t:Tile[T], s:Socket[T], x:Int, y:Int, cx:Int, cy:Int, timeOutId:Int)
  var popTimeOut:Option[PopTimeOut] = None

  def setPopTimeOut(t:Tile[T], s:Socket[T], x:Int, y:Int, cx:Int, cy:Int):Unit = {
    logger.trace("Starting pop timeout")

    cancelPopTimeOut()

    val id = dom.window.setTimeout(
      () => {
        cancelPopTimeOut()
        val freeTile = pullFromSocket(t, s, x, y)
        startDragging(freeTile, cx, cy)
        rerender()
        layout()
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

  /** Called when the user begins dragging a free tile around the canvas */
  def startDragging(item:FreeTile[T], x:Double, y:Double):Unit = {
    dragging = Some(DragInfo(item, item.x, item.y, x, y))
  }

  /**
    * Calculates the relative location of a tile or tile component that is attached and displayed to this tileSpace
    * @param tc the tileComponent whose co-ordinates should be calculated
    * @return
    */
  def relativeLocation(tc:VHtmlComponent):(Int, Int) = {
    val s = scale getOrElse 1.0
    val (tx, ty) = screenLocation(tc)
    val (mx, my) = screenLocation(this)
    (((tx - mx) / s).toInt, ((ty - my) / s).toInt)
  }
  
  /** What to do when a mousedown event occurs on a FreeTile in this space */
  def onMouseDown(ft:FreeTile[T], e:MouseEvent):Unit = {
    if (ft.mobile && freeTiles.contains(ft)) {
      bringToFront(ft);
      layout()
      startDragging(ft, e.clientX, e.clientY)
    }
  }
  

  /** What to do when a mousedown event occurs on a Tile in this space */
  def onMouseDown(t:Tile[T], e:MouseEvent):Unit = {
    t.within match {
      case Some(s:Socket[T]) =>
        val (x, y) = relativeLocation(t)
        setPopTimeOut(t, s, x, y, e.clientX.toInt, e.clientY.toInt)

      case _ => // Nothing to do
    }
  }
  
  val onMouseDrag: MouseEvent => Unit = { (e) =>
    for {
      DragInfo(freetile, ix, iy, mx, my) <- dragging
    } {
      val (tx, ty) = relativeLocation(freetile)
      resetPopTimeOut(tx, ty, e.clientX.toInt, e.clientY.toInt)

      val x = e.clientX
      val y = e.clientY
      val s = scale getOrElse 1.0
      val newTx = ix + (x - mx) / s
      val newTy = iy + (y - my) / s
      freetile.setPosition(newTx, newTy)

      def dist(a:Double, b:Double) = Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2))

      val closestSockets = for {
        ft <- freeTiles
        (sx, sy, socket) <- ft.tile.emptySockets
        freeParent <- socket.freeParent if freeParent != freetile // a tile can't drop into its own sockets
        distance = dist(freeParent.x + sx - newTx, freeParent.y + sy - newTy) if distance < 50 // limit to nearby sockets
      } yield {
        distance -> socket
      }

      activeSocket = if (closestSockets.isEmpty) None else Some(closestSockets.minBy(_._1)._2)
      rerender()
    }
  }

  val onMouseUp: MouseEvent => Unit = { (e) =>
    cancelPopTimeOut()

    for {
      s <- activeSocket
      DragInfo(t:FreeTile[T], _, _, _, _) <- dragging
    } dropIntoSocket(t, s)

    dragging = None
    activeSocket = None
    layout()
    rerender()
  }

  /**
    * Called when a FreeTile joins a Socket
    * @param t
    * @param s
    */
  private def dropIntoSocket(t:FreeTile[T], s:Socket[T]):Unit = {
    TileSpace.logger.debug(s"Dropped $t into $s")

    freeTiles.remove(freeTiles.indexOf(t))
    s.onFilledWith(t.tile)
    t.tile.onPlacedInSocket(s)
  }

  /** Removes a tile from a socket, placing it in a new FreeTile */
  private def pullFromSocket(t:Tile[T], s:Socket[T], x:Int, y:Int):FreeTile[T] = {
    if (!t.within.contains(s)) {
      logger.warn(s"Tried to pull $t from $s but it wasn't within it")
    }

    s.onRemoved(t)
    val ft = FreeTile(t)
    ft.setPosition(x, y)
    t.onRemovedFromSocket(s, x, y)
    t.onPlacedInFreeTile(ft)
    freeTiles.append(ft)

    layout()
    ft
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

  /** Moves a Free Tile to the front of the z-order. In SVG, this is done by the order of elements within the SVG. */
  def bringToFront(t:FreeTile[T]):Unit = {
    freeTiles.remove(freeTiles.indexOf(t))
    freeTiles.append(t)
    rerender()
  }

  /**
    * Adds a free tile to the centre of the canvas
    */
  def addTileToMiddle(tile:FreeTile[T]) = {
    for {
      s <- domNode
      t = s.scrollTop
      l = s.scrollLeft
      ch = s.clientHeight
      cw = s.clientWidth
    } {
      tile.x = (t + ch / 2 + Random.nextInt(10) - 5).toInt
      tile.y = (l + cw / 2 + Random.nextInt(10) - 5).toInt
    }
    freeTiles.append(tile)
    update()
    layout()
  }
  
  def addTiles(tiles:((Double, Double), Tile[T])*):Unit = {
    for {
      ((x, y), tile) <- tiles 
    } {
      val ft = FreeTile(tile)
      ft.setPosition(x, y)
      tile.onPlacedInFreeTile(ft)
      freeTiles.append(ft)
    }
    
    update()
    layout()     
  }

  /**
    * If this TileSpace is attached (and in the page), determines any scale that has been applied to it through CSS.
    */
  def scale = {
    domNode map { case e:SVGElement =>
      val bcrw = e.getBoundingClientRect().width
      val cw = canvasSize._1
      val s = bcrw / cw
      s
    }
  }

  def layout(): Unit = {
    for { t <- freeTiles} t.layout()
  }
}

object TileSpace {

  val logger:Logger = Logger.getLogger(TileSpace.getClass)

  def screenLocation(n:VHtmlComponent):(Int, Int) = {
    n.domNode.map { e =>
      val r = e.getBoundingClientRect()
      (r.left.toInt, r.top.toInt)
    } getOrElse (0, 0)
  }

}
