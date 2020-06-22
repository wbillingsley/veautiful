package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{HBox, Tile, TileSpace, TileText}


class VariableTile(tileSpace:TileSpace[JSExpr], name:String) extends Tile(tileSpace) {

  override val tileContent = {
    HBox(
      TileText[JSExpr](name)
    )
  }

  override def toLanguage: JSExpr = JSVariable(name)

  override def returnType: String = "any"
}