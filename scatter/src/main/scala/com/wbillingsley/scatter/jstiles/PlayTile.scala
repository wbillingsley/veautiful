package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Socket, Tile, TileButton, TileComponent, TileSpace, TileText, VBox}

class PlayTile(tileSpace:TileSpace, cls:String = "btn btn-primary") extends Tile(tileSpace) {

  val socket = new Socket(this)

  val play = new TileButton("play", {
    println("Clicked!")
  }, cls)

  override def returnType: String = "void"

  override val tileContent: TileComponent = {
    VBox(
      play,
      socket
    )
  }

}
