package com.wbillingsley.scatter

import com.wbillingsley.scatter.Tile.{boxAndArc, logger}
import com.wbillingsley.veautiful.html.<.{VDOMElement, VSVGElement}
import com.wbillingsley.veautiful.html.{<, DElement, SVG, VHtmlDiffNode, VHtmlNode, ^}
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
  final def freeParent:Tile[T]  = within.within match {
    case Some(s) => s.freeParent
    case _ => within
  }

  def emptySockets:Seq[(Int, Int, Socket[T])] = {
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

  def emptyPath:VSVGElement = {
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

  override def render: VHtmlDiffNode = {
    SVG.g(^.cls := cssClass, ^.attr("transform") := s"translate($x, $y)",
      content match {
        case Some(t) => t
        case _ => emptyPath
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

  def path[T](tc:Option[Tile[T]]):VHtmlNode = {
    logger.trace(s"Calculating tile path for $tc")
    val (w, h) = tc.flatMap(_.size) getOrElse (15, 15)
    <("path", ns=DElement.svgNS)(^.attr("d") := boxAndArc(w, h))
  }


}