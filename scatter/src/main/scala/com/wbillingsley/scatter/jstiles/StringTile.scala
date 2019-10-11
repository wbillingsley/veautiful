package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{Tile, TileComponent, TileSpace, TileText}
import com.wbillingsley.veautiful.{DElement, DiffComponent, SVG, ^}

case class StringTile(tileSpace:TileSpace[JSExpr], text:String) extends Tile(tileSpace) with DiffComponent {

  override val tileContent: TileComponent[JSExpr] = TileText(s""""${text}"""")

  override def returnType: String = "String"

  override def toLanguage: JSExpr = JSBlank
}
