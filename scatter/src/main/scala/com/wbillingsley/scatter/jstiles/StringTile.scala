package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{Tile, TileComponent, TileSpace, TileText}
import com.wbillingsley.veautiful.DiffComponent

case class StringTile(tileSpace:TileSpace[JSExpr], text:String) extends Tile(tileSpace) {

  override val tileContent: TileComponent[JSExpr] = TileText(s""""${text}"""")

  override def returnType: String = "String"

  override def toLanguage: JSExpr = JSString(text)
}
