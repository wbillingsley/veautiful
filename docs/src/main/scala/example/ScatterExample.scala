package example

import com.wbillingsley.scatter.jstiles.{IfElseTile, JSLang, NumberInputTile, PlayTile, StringTile}
import com.wbillingsley.scatter.TileSpace
import com.wbillingsley.veautiful.{<, VNode}

object ScatterExample {

  val scatterCanvas = new TileSpace(Some("example"), JSLang)()
  scatterCanvas.tiles.append(
    new PlayTile(scatterCanvas),
    StringTile(scatterCanvas, "Hello world"),
    StringTile(scatterCanvas, "Another string"),
    new NumberInputTile(scatterCanvas),
    new IfElseTile(scatterCanvas)
  )


  def page:VNode = Common.layout(<.div(
    scatterCanvas
  ))

}
