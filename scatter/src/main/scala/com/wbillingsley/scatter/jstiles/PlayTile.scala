package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Socket, Tile, TileComponent, TileSpace, TileText}

class PlayTile(tileSpace:TileSpace) extends Tile(tileSpace) {

  val socket = new Socket(this)

  override def returnType: String = "void"

  override def tileContent: TileComponent = {
    HBox(
      TileText("play!"),
      socket
    )
  }

}
