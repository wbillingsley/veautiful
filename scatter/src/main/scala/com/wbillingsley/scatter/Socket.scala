package com.wbillingsley.scatter

import com.wbillingsley.scatter.Tile.{boxAndArc, logger}
import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.{<, DElement, DiffComponent, DiffNode, OnScreen, VNode, ^}
import org.scalajs.dom.raw.SVGElement

class Socket(within:Tile) extends TileComponent {

  var content:Option[Tile] = None

  override def render: DiffNode = {
    <("g", ns = DElement.svgNS)(^.cls := "socket", ^.attr("transform") := s"translate($x, $y)",
      Socket.path(content)
    )
  }

}

object Socket {

  val logger:Logger = Logger.getLogger(Socket.getClass)

  def path(tc:Option[Tile]):VNode = {
    logger.trace(s"Calculating tile path for $tc")
    val (w, h) = tc.flatMap(_.size) getOrElse (20, 20)
    <("path", ns=DElement.svgNS)(^.attr("d") := boxAndArc(w, h))
  }


}