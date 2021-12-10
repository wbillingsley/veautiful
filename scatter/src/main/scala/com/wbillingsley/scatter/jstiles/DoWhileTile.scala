package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter._

class DoWhileTile(tileSpace:TileSpace[JSExpr]) extends Tile(tileSpace) {

  val condition = new Socket(this, acceptType = Some("Boolean"))
  val block = new SocketList(this, acceptType = Some("void"))

  override def returnType: String = "void"

  override val tileContent = {
    VBox(
      TileText("do {"),
      HBox(TileText("  "), block),
      HBox(TileText("} while ("), condition, TileText(")")),
    )
  }

  def toLanguage:JSExpr = JSDoWhile(
    JSBlock(
      block.sockets.flatMap(_.content).map(_.toLanguage)
    ),
    condition.content.map(_.toLanguage) getOrElse JSBlank
  )

}