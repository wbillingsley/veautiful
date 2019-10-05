package com.wbillingsley.scatter

import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.{<, DElement, DiffComponent, DiffNode, Layout, OnScreen, SVG, Update, VNode, ^}
import org.scalajs.dom.raw.{HTMLElement, MouseEvent, SVGElement}

abstract class Tile(val ts:TileSpace) extends OnScreen with DiffComponent {

  import Tile._

  def free:Boolean = within.isEmpty

  var within:Option[Socket] = None

  def sockets:Seq[Socket] = Seq.empty

  def returnType:String

  def onMouseDown(e:MouseEvent):Unit = {
    ts.onMouseDown(this, e)
  }

  def registerDragListeners():Unit = {
    for { n <- domNode } {
      n.addEventListener("pointerdown", onMouseDown)
    }
  }

  override def render: DiffNode = {
    logger.trace(s"render called on $this")

    val c = tileContent

    SVG.g(^.cls := "tile", ^.attr("transform") := s"translate($x, $y)",
      tileBoundary,
      SVG.g(^.attr("transform") := s"translate(${Tile.boxStartX + Tile.padding}, ${Tile.padding})", c)
    )
  }

  /**
    * The elements to render inside the tile. At the moment, this has to be a val, because components will try to
    * get their bounds from their domNode (so we need to be able to ask tileContent.size and not generate new
    * unattached nodes)
    */
  val tileContent:TileComponent

  val tileBoundary:DElement = SVG.path(^.cls := "tile-path")

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

  def layout():Unit = {
    tileContent.layoutChildren()
    for { el <- tileBoundary.domEl } {
      el.setAttribute("d", Tile.path(tileContent))
    }
  }

}

object Tile {

  val logger:Logger = Logger.getLogger(Tile.getClass)

  def path(tc:TileComponent):String = {
    logger.trace(s"Calculating tile path for $tc")
    val (w, h) = tc.size getOrElse (20, 20)
    boxAndArc(w, h)
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

  def boxAndArc(w:Int, h:Int):String = {
    // Work out the width and height with padding
    val pw = w + 2 * padding
    val ph = h + 2 * padding

    s"$typeCorner l $pw 0 l 0 $ph l ${-pw} 0 z"
  }

}




