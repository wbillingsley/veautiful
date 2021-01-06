package example

import com.wbillingsley.scatter.TileSpace
import com.wbillingsley.scatter.jstiles._
import com.wbillingsley.veautiful.html.{<, VHtmlNode, ^}

import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}

@JSExportTopLevel("ScatterExample")
object ScatterExample {

  @JSExport
  val scatterCanvas = new TileSpace(Some("example"), JSLang)()

  val pt = new ProgramTile(scatterCanvas, <.button(^.cls := "btn btn-sm btn-primary", ^.onClick --> run(), "Run"))

  @JSExport
  val items = Seq(
    (0d, 0d) -> pt,
    (50d, 50d) -> StringTile(scatterCanvas, "Hello world"),
    (70d, 70d) -> StringTile(scatterCanvas, "Another string"),
    (100d, 100d) -> new FunctionCallTile(scatterCanvas, "console.log", Seq("String")),
    (0d, 0d) -> new NumberInputTile(scatterCanvas),
    (0d, 0d) -> new IfElseTile(scatterCanvas)
  )

  scatterCanvas.addTiles(items:_*)

  val output = <.textarea(^.attr("placeholder") := "Program will appear here")

  def run():Unit = {
    output.makeItSo(<.textarea(^.attr("placeholder") := "Program will appear here", pt.toLanguage.toJS(0)))
  }

  def page:VHtmlNode = <.div(^.cls := "row",
    <.div(^.cls := "col", scatterCanvas),
    <.div(^.cls := "col", output)
  )

}
