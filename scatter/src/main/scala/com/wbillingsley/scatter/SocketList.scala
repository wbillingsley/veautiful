package com.wbillingsley.scatter
import com.wbillingsley.veautiful.{DiffNode, SVG, ^}

class SocketList[T](val within:Tile[T], acceptType:Option[String] = None) extends TileComponent[T] {

  var sockets:Seq[Socket[T]] = Seq(new Socket(within, acceptType, false, refreshSockets))

  var content = VBox(sockets:_*)

  def refreshSockets(socket: Socket[T]):Unit = {
    val filled = sockets.filter(_.content.nonEmpty)
    sockets = (for {
      existing <- filled
      s <- Seq(new Socket(within, acceptType, true, refreshSockets), existing)
    } yield s) :+ new Socket(within, acceptType, filled.nonEmpty, refreshSockets)

    content = VBox(sockets:_*)

    rerender()
    within.ts.layout()
  }

  override def emptySockets: Seq[(Int, Int, Socket[T])] = {
    for {
      s <- sockets if s.content.isEmpty
    } yield (s.x, s.y, s)
  }

  override protected def render: DiffNode = SVG.g(^.cls := "socket-list", ^.attr("transform") := s"translate($x, $y)",
    content
  )

  override def layoutChildren(): Unit = {
    super.layoutChildren()
    content.layoutChildren()
  }

}
