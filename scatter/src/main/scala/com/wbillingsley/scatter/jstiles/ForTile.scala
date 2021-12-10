package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter._

class ForTile(tileSpace:TileSpace[JSExpr]) extends Tile(tileSpace) {

  val before = new Socket(this, acceptType = Some("void"))
  val condition = new Socket(this, acceptType = Some("Boolean"))
  val after = new Socket(this, acceptType = Some("void"))
  val block = new SocketList(this, acceptType = Some("void"))

  override def returnType: String = "void"

  override val tileContent = {
    VBox(
      HBox(TileText("for ("), before, TileText("; "), condition, TileText("; "), after, TileText(") {")),
      HBox(TileText("  "), block),
      TileText("}")
    )
  }

  def toLanguage:JSExpr = JSFor(
    JSBlock(Seq(before.content.map(_.toLanguage) getOrElse JSBlank)),
    condition.content.map(_.toLanguage) getOrElse JSBlank,
    JSBlock(Seq(after.content.map(_.toLanguage) getOrElse JSBlank)),
    JSBlock(
      block.sockets.flatMap(_.content).map(_.toLanguage)
    )
  )

}