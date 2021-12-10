package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter._

class DeleteTile (tileSpace:TileSpace[JSExpr], cls:String = "btn btn-danger") extends Tile(tileSpace, false, TypeLoopMode.Never, cssClass = "delete") {

  var remembered:Option[Tile[JSExpr]] = None

  val socket = new Socket[JSExpr](this, onChange = { s =>
    // remember the tile for undo
    remembered = s.content

    // delete the content
    s.content = None
  })

  override def returnType: String = ""

  override val tileContent = {
    VBox(
      TileText("Trash"),
      socket
    )
  }

  override def toLanguage: JSExpr = JSBlank

  def undo():Unit = {
    println("Undo")
  }
}

case class LessThan(left:JSExpr, right: JSExpr) extends JSExpr {
  override def toJS(indent: Int): String = {
    s"${left.toJS(indent)} < ${right.toJS(indent) }"
  }
}

class LessThanTile(tileSpace:TileSpace[JSExpr], override val returnType:String = "Boolean") extends Tile(tileSpace) {

  val left = new Socket[JSExpr](this, acceptType = Some("Number"))
  val right = new Socket[JSExpr](this, acceptType = Some("Number"))

  override val tileContent = {
    HBox(
      left,
      TileText[JSExpr](" < "),
      right
    )
  }

  override def toLanguage: JSExpr = LessThan(
    left.content.map(_.toLanguage).getOrElse(JSBlank),
    right.content.map(_.toLanguage).getOrElse(JSBlank)
  )

}