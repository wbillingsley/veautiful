package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Socket, SocketList, Tile, TileComponent, TileForeignObject, TileSpace, TileText, VBox}

class FunctionCallTile(tileSpace:TileSpace[JSExpr], name:String, params:Seq[String]) extends Tile(tileSpace) {

  override def returnType: String = "void"

  val sockets = params.map { n => new Socket[JSExpr](this, Some(n)) }

  val s:Seq[TileComponent[JSExpr]] = {
    if (sockets.isEmpty) Seq.empty else sockets.map(Seq(_)).reduce[Seq[TileComponent[JSExpr]]]({ _ ++ Seq(TileText[JSExpr](", ")) ++ _ })
  }

  override val tileContent = {
    HBox(
      (TileText[JSExpr](name + "(") +: s :+ TileText[JSExpr](")")) :_*
    )
  }

  override def toLanguage: JSExpr = FunctionCall(name, sockets.map(_.content.map(_.toLanguage) getOrElse JSBlank))

}