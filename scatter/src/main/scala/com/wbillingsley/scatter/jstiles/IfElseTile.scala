package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Socket, SocketList, Tile, TileComponent, TileSpace, TileText, VBox}

class IfElseTile(tileSpace:TileSpace[JSExpr]) extends Tile(tileSpace) {

  val condition = new Socket(this, acceptType = Some("Boolean"))
  val trueCase = new SocketList(this, acceptType = Some("void"))
  val falseCase = new SocketList(this, acceptType = Some("void"))

  override def returnType: String = "void"

  override val tileContent = {
    VBox(
      HBox(TileText("if ("), condition, TileText(") {")),
      HBox(TileText("  "), trueCase),
      HBox(TileText("} else {")),
      HBox(TileText("  "), falseCase),
      TileText("}")
    )
  }

  def toLanguage:JSExpr = {
    JSBlank
  }

}