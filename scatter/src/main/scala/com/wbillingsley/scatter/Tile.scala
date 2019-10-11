package com.wbillingsley.scatter

import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.{<, DElement, DiffComponent, DiffNode, Layout, OnScreen, SVG, Update, VNode, ^}
import org.scalajs.dom.raw.{HTMLElement, MouseEvent, SVGElement}

abstract class Tile[T](val ts:TileSpace[T], val mobile:Boolean = true, val typeLoop:Boolean = true, val cssClass:String = "") extends OnScreen with DiffComponent {

  import Tile._

  def free:Boolean = within.isEmpty

  var within:Option[Socket[T]] = None

  def returnType:String

  /**
    * Called by the tileSpace if this tile is dropped into a socket, to update its internal state
    * @param s the socket it is dropped into
    */
  def onPlacedInSocket(s:Socket[T]):Unit = {
    setPosition(0,0)
    within = Some(s)
    if (ts.activeTile.contains(this)) {
      ts.activeTile = None
    }
  }

  /**
    * Called by the tileSpace if this tiled is pulled from a socket, to update its internal state
    * @param s the socket it was pulled from
    * @param x the x location the tile should move to
    * @param y the y location the tile should move to
    */
  def onRemovedFromSocket(s:Socket[T], x:Int, y:Int):Unit = {
    setPosition(x, y)
    within = None
  }

  def onMouseDown(e:MouseEvent):Unit = {
    logger.trace(s"Mousedown on $this")
    e.stopPropagation()
    ts.onMouseDown(this, e)
  }

  def onMouseOver(e:MouseEvent):Unit = {
    logger.trace(s"Mouse over $this")
    ts.activeTile = Some(this)
    e.stopPropagation()
    rerender()
  }

  def onMouseOut(e:MouseEvent):Unit = {
    logger.trace(s"Mouse out $this")
    if (ts.activeTile.contains(this)) ts.activeTile = None
    e.stopPropagation()
    rerender()
  }


  def emptySockets:Seq[(Int, Int, Socket[T])] = for {
    (x, y, e) <- tileContent.emptySockets
  } yield (contentOffsetX + x, contentOffsetY + y, e)

  def registerDragListeners():Unit = {
    for { n <- domNode } {
      n.addEventListener("pointerdown", onMouseDown)
      n.addEventListener("pointerout", onMouseOut)
      n.addEventListener("pointerover", onMouseOver)
    }
  }

  def contentOffsetX:Int = {
    if (typeLoop) Tile.boxStartX + Tile.padding else Tile.padding
  }

  def contentOffsetY:Int = Tile.padding

  override def render: DiffNode = {
    logger.trace(s"render called on $this")

    val c = tileContent
    val (w, h) = c.size getOrElse (20,20)

    def classString: String = {
      var str = "tile " + cssClass
      if (within.nonEmpty) str += " contained"
      if (ts.activeTile.contains(this)) str += " mouseover"
      str
    }

    if (within.isEmpty) {
      SVG.g(^.cls := classString, ^.attr("transform") := s"translate($x, $y)",
        tileBoundary,
        SVG.g(^.cls := "type-icon", ts.language.nodeIcon(returnType)),
        SVG.g(^.attr("transform") := s"translate($contentOffsetX, $contentOffsetY)", c)
      )
    } else {
      SVG.g(^.cls := classString,
        SVG.rect(^.attr("width") := w, ^.attr("height") := h),
        c
      )
    }
  }

  /**
    * The elements to render inside the tile. At the moment, this has to be a val, because components will try to
    * layout their tileContent (so we need to be able to call tileContent.layoutChildren and not generate new
    * unattached nodes that get uselessly laid out)
    */
  val tileContent:TileComponent[T]

  val tileBoundary:DElement = SVG.path(^.cls := "tile-path")

  override def afterAttach(): Unit = {
    super.afterAttach()
    if (mobile) registerDragListeners()
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

  def layout():Unit = {
    tileContent.layoutChildren()
    for { el <- tileBoundary.domEl } {
      el.setAttribute("d", Tile.path(tileContent, typeLoop))
    }
  }

  def toLanguage:T

}

object Tile {

  val logger:Logger = Logger.getLogger(Tile.getClass)

  def path[T](tc:TileComponent[T], typeLoop:Boolean = true):String = {
    logger.trace(s"Calculating tile path for $tc")
    val (w, h) = tc.size getOrElse (20, 20)
    if (typeLoop) boxAndArc(w, h) else boxOnly(w, h)
  }

  def loopLessPath[T](tc:TileComponent[T]):String = {
    logger.trace(s"Calculating loopless path for $tc")
    val (w, h) = tc.size getOrElse (20, 20)
    boxAndArc(w, h)
  }

  def corner:String = "M 9 3 l 0 -3 "

  val padding = 3

  val typeLoopRadius = 9

  val typeLoopY1 = 15

  val typeLoopY2 = 4

  val boxStartX = 14

  val typeCentreX = 6

  val typeCentreY = 10

  val contentStart:(Int, Int) = (16, 2)

  val fontSize:Int = 15

  val typeCorner:String = s"M $boxStartX $typeLoopY1 A $typeLoopRadius $typeLoopRadius 0 1 1 $boxStartX $typeLoopY2 L $boxStartX 0 "

  def boxAndArc(w:Int, h:Int):String = {
    // Work out the width and height with padding
    val pw = w + 2 * padding
    val ph = h + 2 * padding

    s"$typeCorner l $pw 0 l 0 $ph l ${-pw} 0 z"
  }

  def boxOnly(w:Int, h:Int):String = {
    val pw = w + 2 * padding
    val ph = h + 2 * padding
    s"M 0 0 l $pw 0 l 0 $ph l -$pw 0 z"
  }

}




