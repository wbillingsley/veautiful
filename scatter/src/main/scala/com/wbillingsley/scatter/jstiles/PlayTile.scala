package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{Socket, Tile, TileForeignObject, TileSpace, VBox, TypeLoopMode}
import com.wbillingsley.veautiful.html.{<, ^}

class PlayTile(tileSpace:TileSpace[JSExpr], cls:String = "btn btn-primary") extends Tile(tileSpace, false, TypeLoopMode.Never, cssClass = "play") {

  val socket = new Socket(this)

  override def returnType: String = "void"

  override val tileContent = {
    VBox(
      TileForeignObject(<.button("play", ^.cls := "btn btn-sm btn-primary", ^.onClick --> println("Hooray!"))),
      socket
    )
  }
  override def toLanguage: JSExpr = JSBlank
}
