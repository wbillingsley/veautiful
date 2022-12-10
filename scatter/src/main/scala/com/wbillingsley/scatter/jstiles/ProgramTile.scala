package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{Socket, SocketList, Tile, TileForeignObject, TileSpace, VBox, TypeLoopMode}
import com.wbillingsley.veautiful.dom.DDomContent
import com.wbillingsley.veautiful.VNode

class ProgramTile(tileSpace:TileSpace[JSExpr], html:DDomContent) extends Tile(tileSpace, false, TypeLoopMode.Never, cssClass = "play") {

  val socketList = new SocketList(this)

  override def returnType: String = "void"

  override val tileContent = {
    VBox(
      TileForeignObject(html),
      socketList
    )
  }

  override def toLanguage: JSExpr = JSBlock(
    socketList.sockets.flatMap(_.content).map(_.toLanguage)
  )

}
