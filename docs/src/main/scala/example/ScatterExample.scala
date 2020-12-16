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

  def page:VHtmlNode = Common.layout(<.div(^.cls := "row",
    <.div(^.cls := "col", scatterCanvas),
    <.div(^.cls := "col", output)
  ))

}
