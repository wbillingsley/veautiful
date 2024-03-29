package com.wbillingsley.scatter

import com.wbillingsley.scatter.Tile.{boxAndArc, logger}
import com.wbillingsley.veautiful.html.{<, DElement, VHtmlDiffNode, VDomNode, ^}
import com.wbillingsley.veautiful.svg.{SVG, DSvgElement, DSvgContent}
import com.wbillingsley.veautiful.logging.Logger

import scala.annotation.tailrec

class Socket[T](val within:Tile[T], acceptType:Option[String] = None, thin:Boolean = false, onChange: (Socket[T]) => Unit = { (x:Socket[T]) => }) extends TileComponent[T] {

  var content:Option[Tile[T]] = None

  def tileSpace:TileSpace[T] = within.ts

  /**
    * Called by the tileSpace when the socket is filled to update its internal state
    * @param t the tile dropped into the socket
    */
  def onFilledWith(t:Tile[T]):Unit = {
    content = Some(t)
    onChange(this)
  }

  /**
    * Called by the tileSpace when a tile is removed from the socket, to update its internal state
    * @param t
    */
  def onRemoved(t:Tile[T]):Unit = {
    content = None
    onChange(this)
  }

  /**
    * Traverses the tile structure to find which "free tile" (tile not in a socket) this socket belongs to
    * @return
    */
  @tailrec
  final def freeParent:Option[FreeTile[T]]  = within.within match {
    case Some(s:Socket[T]) => s.freeParent
    case Some(ft:FreeTile[T]) => Some(ft)
    case None => None
  }

  def emptySockets:Seq[(Int, Int, Socket[T])] = {
    content match {
      case Some(t) => t.emptySockets
      case _ =>
        Seq((0, 0, this))
    }
  }

  /**
    * True if the socket should be highlighted as active for a tile drop on mouse-up
    * @return
    */
  def active:Boolean = tileSpace.activeSocket.contains(this)

  def emptyPath:DSvgContent = {
    if (thin) {
      SVG.g(
        SVG.path(^.attr("d") := "M 0 0 l 20 0")
      )
    } else {
      SVG.g(
        Socket.path(content),
        SVG.g(^.cls := "socket-type-icon", within.ts.language.socketIcon(acceptType)),
      )
    }
  }

  def cssClass:String = {
    var s = "socket"
    if (active) s += " active"
    if (thin) s += " thin"
    s
  }

  override def render = {
    SVG.g(^.cls := cssClass, ^.attr("transform") := s"translate($x, $y)",
      content match {
        case Some(t) => t
        case _ => emptyPath
      }
    )
  }

  override def size: Option[(Int, Int)] = {
    content match {
      case Some(t) => t.size
      case None => if thin then Some(20, 2) else Some(35, 15)
    }
  }

  override def layoutChildren(): Unit = {
    super.layoutChildren()
    content.foreach(_.layout())
  }

}

object Socket {

  val logger:Logger = Logger.getLogger(Socket.getClass)

  def path[T](tile:Option[Tile[T]]) = {
    logger.trace(s"Calculating tile path for $tile")
    val (w, h) = tile.flatMap(_.tileContent.size) getOrElse (15, 15)
    SVG.path(^.attr("d") := boxAndArc(w, h))
  }


}