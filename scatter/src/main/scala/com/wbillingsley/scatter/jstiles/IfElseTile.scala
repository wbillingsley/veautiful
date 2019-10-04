package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Socket, Tile, TileComponent, TileSpace, TileText, VBox}

class IfElseTile(tileSpace:TileSpace) extends Tile(tileSpace) {

  val condition = new Socket(this)
  val trueCase = new Socket(this)
  val falseCase = new Socket(this)

  override def returnType: String = "void"

  override val tileContent: TileComponent = {
    VBox(
      HBox(TileText("if "), condition, TileText("{")),
      trueCase,
      HBox(TileText("} else {")),
      falseCase,
      TileText("}")
    )
  }

}