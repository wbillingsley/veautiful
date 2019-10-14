package example

import com.wbillingsley.scatter.jstiles.{FunctionCallTile, IfElseTile, JSLang, NumberInputTile, PlayTile, ProgramTile, StringTile}
import com.wbillingsley.scatter.{Socket, TileSpace}
import com.wbillingsley.veautiful.{<, VNode, ^}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ScatterExample")
object ScatterExample {

  @JSExport
  val scatterCanvas = new TileSpace(Some("example"), JSLang)()

  val pt = new ProgramTile(scatterCanvas, <.button(^.cls := "btn btn-sm btn-primary", ^.onClick --> run, "Run"))

  @JSExport
  val items = Seq(
    pt,
    StringTile(scatterCanvas, "Hello world"),
    StringTile(scatterCanvas, "Another string"),
    new FunctionCallTile(scatterCanvas, "console.log", Seq("String")),
    new NumberInputTile(scatterCanvas),
    new IfElseTile(scatterCanvas)
  )

  scatterCanvas.tiles.appendAll(items)

  val output = <.textarea(^.attr("placeholder") := "Program will appear here")

  def run():Unit = {
    output.makeItSo(<.textarea(^.attr("placeholder") := "Program will appear here", pt.toLanguage.toJS(0)))
  }

  def page:VNode = Common.layout(<.div(^.cls := "row",
    <.div(^.cls := "col", scatterCanvas),
    <.div(^.cls := "col", output)
  ))

}
