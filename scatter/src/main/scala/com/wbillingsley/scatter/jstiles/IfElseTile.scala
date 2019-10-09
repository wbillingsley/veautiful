package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Socket, Tile, TileComponent, TileSpace, TileText, VBox}

class IfElseTile(tileSpace:TileSpace) extends Tile(tileSpace) {

  val condition = new Socket(this, acceptType = Some("Boolean"))
  val trueCase = new Socket(this)
  val falseCase = new Socket(this)

  override def returnType: String = "void"

  override val tileContent: TileComponent = {
    VBox(
      HBox(TileText("if ("), condition, TileText(") {")),
      HBox(TileText("  "), trueCase),
      HBox(TileText("} else {")),
      HBox(TileText("  "), falseCase),
      TileText("}")
    )
  }

}