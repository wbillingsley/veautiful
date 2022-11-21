package com.wbillingsley.scatter

import com.wbillingsley.veautiful.html.{DElement, SVG, VHtmlComponent, VHtmlDiffNode, ^, VDOMElement, VSVGElement}
import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.OnScreen
import org.scalajs.dom.{MouseEvent, SVGElement}

import scala.annotation.tailrec

/**
  * A tile can be contained in a socket, or it can be being dragged around the canvas as (in) a FreeTile
  * @tparam T
  */
type TileParent[T] = Socket[T] | FreeTile[T]

/**
  * Whether a tile should display its "type loop"
  */
enum TypeLoopMode {
  case Never, WhenFree, Always
}

abstract class Tile[T](val ts:TileSpace[T], val mobile:Boolean = true, val typeLoopMode:TypeLoopMode = TypeLoopMode.WhenFree, val cssClass:String = "") extends VHtmlComponent {

  import Tile._
  
  var within:Option[TileParent[T]] = None

  /** Whether the tile is contained in a FreeTile */
  def free:Boolean = within match {
    case Some(_:FreeTile[T]) => true
    case _ => false
  }

  def showTypeLoop = typeLoopMode match {
    case TypeLoopMode.Always => true
    case TypeLoopMode.Never => false
    case TypeLoopMode.WhenFree => free
  }

  @tailrec
  final def freeTile:Option[FreeTile[T]] = {
    within match {
      case Some(ft:FreeTile[T]) => Some(ft)
      case Some(s:Socket[T]) => s.within.freeTile
      case None => None
    }
  }

  def returnType:String

  /**
    * Called by the tileSpace if this tile is dropped into a socket, to update its internal state
    * @param s the socket it is dropped into
    */
  def onPlacedInSocket(s:Socket[T]):Unit = {
    within = Some(s)
    if (ts.activeTile.contains(this)) {
      ts.activeTile = None
    }
    rerender()
  }

  /**
    * Called by the tileSpace if this tiled is pulled from a socket, to update its internal state
    * @param s the socket it was pulled from
    * @param x the x location the tile should move to
    * @param y the y location the tile should move to
    */
  def onRemovedFromSocket(s:Socket[T], x:Int, y:Int):Unit = {
    within = None
  }
  
  def onPlacedInFreeTile(ft:FreeTile[T]):Unit = {
    within = Some(ft)
  }

  val onMouseDown: MouseEvent => Unit = { (e) =>
    logger.trace(s"Mousedown on $this")
    e.stopPropagation()
    ts.onMouseDown(this, e)
    
    for ft <- freeTile do ts.onMouseDown(ft, e)
  }

  val onMouseOver: MouseEvent => Unit = { (e) =>
    logger.trace(s"Mouse over $this")
    ts.activeTile = Some(this)
    e.stopPropagation()
    rerender()
  }

  val onMouseOut: MouseEvent => Unit = { (e) =>
    logger.trace(s"Mouse out $this")
    if (ts.activeTile.contains(this)) ts.activeTile = None
    e.stopPropagation()
    rerender()
  }


  def emptySockets:Seq[(Int, Int, Socket[T])] = for {
    (x, y, e) <- tileContent.emptySockets
  } yield (contentOffsetX + x, contentOffsetY + y, e)

  def registerMouseListeners():Unit = {
    for { n <- domNode } {
      n.addEventListener("pointerdown", onMouseDown)
      n.addEventListener("pointerout", onMouseOut)
      n.addEventListener("pointerover", onMouseOver)
    }
  }

  override def afterAttach(): Unit = {
    super.afterAttach()
    if (mobile) registerMouseListeners()
  }

  def contentOffsetX:Int = {
    if (showTypeLoop) Tile.boxStartX + Tile.padding else Tile.padding
  }

  def contentOffsetY:Int = Tile.padding

  override def render: VHtmlDiffNode = {
    logger.trace(s"render called on $this")

    val (w, h) = tileContent.size getOrElse (20,20)

    def classString: String = {
      var str = "tile " + cssClass
      if (within.nonEmpty) str += " contained"
      if (ts.activeTile.contains(this)) str += " mouseover"
      str
    }

    if (free) {
      SVG.g(^.cls := classString, 
        tileBoundary,
        SVG.g(^.cls := "type-icon", ts.language.nodeIcon(returnType)),
        SVG.g(^.attr("transform") := s"translate($contentOffsetX, $contentOffsetY)", tileContent)
      )
    } else {
      SVG.g(^.cls := classString,
        SVG.rect(^.attr("width") := w, ^.attr("height") := h),
        tileContent
      )
    }
  }
  
  def size:Option[(Int, Int)] = {
    if (showTypeLoop)
      for (w, h) <- tileContent.size yield (16 + w, h)
    else
      tileContent.size
  }

  /**
    * The elements to render inside the tile. At the moment, this has to be a val, because components will try to
    * layout their tileContent (so we need to be able to call tileContent.layoutChildren and not generate new
    * unattached nodes that get uselessly laid out)
    */
  val tileContent:TileComponent[T]

  val tileBoundary:VSVGElement = SVG.path(^.cls := "tile-path")

  def layout():Unit = {
    tileContent.layoutChildren()
    for { el <- tileBoundary.domNode } {
      el.setAttribute("d", Tile.path(tileContent, showTypeLoop))
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




