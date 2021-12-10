package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Socket, Tile, TileSpace, TileText}


class LetTile(tileSpace:TileSpace[JSExpr], name:String) extends Tile(tileSpace) {

  override def returnType: String = "void"

  val condition = new Socket(this)

  override val tileContent = {
    HBox(
      TileText[JSExpr](s"let $name = "), condition
    )
  }

  override def toLanguage: JSExpr = JSLet(name, condition.content.map(_.toLanguage) getOrElse JSBlank)

}