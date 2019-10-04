package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter.{Tile, TileComponent, TileSpace, TileText}
import com.wbillingsley.veautiful.{DElement, DiffComponent, SVG, ^}

case class StringTile(tileSpace:TileSpace, text:String) extends Tile(tileSpace) with DiffComponent {

  override val tileContent: TileComponent = TileText(text)

  override def returnType: String = "String"

}
