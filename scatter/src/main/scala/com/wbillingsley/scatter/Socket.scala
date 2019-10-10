package com.wbillingsley.scatter

import com.wbillingsley.scatter.Tile.{boxAndArc, logger}
import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.{<, DElement, DiffComponent, DiffNode, OnScreen, SVG, VNode, ^}
import org.scalajs.dom.raw.SVGElement

import scala.annotation.tailrec

class Socket(val within:Tile, acceptType:Option[String] = None) extends TileComponent {

  var content:Option[Tile] = None

  def tileSpace:TileSpace = within.ts

  /**
    * Called by the tileSpace when the socket is filled to update its internal state
    * @param t the tile dropped into the socket
    */
  def onFilledWith(t:Tile):Unit = {
    content = Some(t)
  }

  /**
    * Called by the tileSpace when a tile is removed from the socket, to update its internal state
    * @param t
    */
  def onRemoved(t:Tile):Unit = {
    content = None
  }

  /**
    * Traverses the tile structure to find which "free tile" (tile not in a socket) this socket belongs to
    * @return
    */
  @tailrec
  final def freeParent:Tile  = within.within match {
    case Some(s) => s.freeParent
    case _ => within
  }

  def emptySockets:Seq[(Int, Int, Socket)] = {
    content match {
      case Some(t) =>
        for  {
          (x, y, e) <- t.emptySockets
        } yield (t.x + x, t.y + y, e)
      case _ =>
        Seq((0, 0, this))
    }
  }

  /**
    * True if the socket should be highlighted as active for a tile drop on mouse-up
    * @return
    */
  def active:Boolean = tileSpace.activeSocket.contains(this)

  override def render: DiffNode = {
    SVG.g(^.cls := (if (active) "socket active" else "socket"), ^.attr("transform") := s"translate($x, $y)",
      content match {
        case Some(t) => t
        case _ => SVG.g(
          Socket.path(content),
          SVG.g(^.cls := "socket-type-icon", within.ts.language.socketIcon(acceptType)),
        )
      }
    )
  }

  override def layoutChildren(): Unit = {
    super.layoutChildren()
    content.foreach(_.layout())
  }

}

object Socket {

  val logger:Logger = Logger.getLogger(Socket.getClass)

  def path(tc:Option[Tile]):VNode = {
    logger.trace(s"Calculating tile path for $tc")
    val (w, h) = tc.flatMap(_.size) getOrElse (15, 15)
    <("path", ns=DElement.svgNS)(^.attr("d") := boxAndArc(w, h))
  }


}