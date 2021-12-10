package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Socket, Tile, TileSpace, TileText}

class AssignmentTile(tileSpace:TileSpace[JSExpr], name:String) extends Tile(tileSpace) {

  override def returnType: String = "void"

  val condition = new Socket(this)

  override val tileContent = {
    HBox(
      TileText[JSExpr](s"$name = "), condition
    )
  }

  override def toLanguage: JSExpr = JSAssign(name, condition.content.map(_.toLanguage) getOrElse JSBlank)

}