package com.wbillingsley.scatter.jstiles

import com.wbillingsley.scatter._

class FunctionDefinitionTile(tileSpace:TileSpace[JSExpr], var name:String, var params:Seq[String]) extends Tile(tileSpace) {

  override def returnType: String = "void"

  val body = new SocketList(this, acceptType = Some("void"))


  override val tileContent = {
    VBox(
      HBox(
        TileText[JSExpr]("function " + name + "(" + params.mkString(",") + ") {")
      ),
      HBox(TileText("  "), body),
      TileText("}")
    )
  }

  override def toLanguage: JSExpr = JSFunction(name, params, JSBlock(body.sockets.flatMap(_.content).map(_.toLanguage)))

}