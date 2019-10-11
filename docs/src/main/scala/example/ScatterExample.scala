package example

import com.wbillingsley.scatter.jstiles.{IfElseTile, JSLang, NumberInputTile, PlayTile, ProgramTile, StringTile}
import com.wbillingsley.scatter.TileSpace
import com.wbillingsley.veautiful.{<, VNode, ^}

object ScatterExample {

  val scatterCanvas = new TileSpace(Some("example"), JSLang)()

  val pt = new ProgramTile(scatterCanvas, <.button(^.cls := "btn btn-sm btn-primary", ^.onClick --> run, "Run"))

  scatterCanvas.tiles.append(
    pt,
    StringTile(scatterCanvas, "Hello world"),
    StringTile(scatterCanvas, "Another string"),
    new NumberInputTile(scatterCanvas),
    new IfElseTile(scatterCanvas)
  )

  val output = <.textarea(^.attr("placeholder") := "Program will appear here")

  def run():Unit = {
    output.makeItSo(<.textarea(^.attr("placeholder") := "Program will appear here", pt.toLanguage.toJS(0)))
  }

  def page:VNode = Common.layout(<.div(^.cls := "row",
    <.div(^.cls := "col", scatterCanvas),
    <.div(^.cls := "col", output)
  ))

}
