package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Socket, Tile, TileButton, TileComponent, TileForeignObject, TileSpace, TileText, VBox}
import com.wbillingsley.veautiful.{<, ^}

class PlayTile(tileSpace:TileSpace, cls:String = "btn btn-primary") extends Tile(tileSpace) {

  val socket = new Socket(this)

  override def returnType: String = "void"

  override val tileContent: TileComponent = {
    VBox(
      TileForeignObject(<.button("play", ^.cls := "btn btn-sm btn-primary", ^.onClick --> println("Hooray!"))),
      socket
    )
  }

}
