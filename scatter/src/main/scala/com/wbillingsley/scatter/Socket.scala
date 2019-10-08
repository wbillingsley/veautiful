package com.wbillingsley.scatter

import com.wbillingsley.scatter.Tile.{boxAndArc, logger}
import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.{<, DElement, DiffComponent, DiffNode, OnScreen, SVG, VNode, ^}
import org.scalajs.dom.raw.SVGElement

class Socket(val within:Tile) extends TileComponent {

  var content:Option[Tile] = None

  def tileSpace:TileSpace = within.ts

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

  def active:Boolean = tileSpace.activeSocket.contains(this)

  override def render: DiffNode = {
    SVG.g(^.cls := (if (active) "socket active" else "socket"), ^.attr("transform") := s"translate($x, $y)",
      content match {
        case Some(t) => t
        case _ => Socket.path(content)
      }
    )
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