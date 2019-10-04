package example

import com.wbillingsley.scatter.jstiles.{PlayTile, StringTile}
import com.wbillingsley.scatter.TileSpace
import com.wbillingsley.veautiful.{<, VNode}

object ScatterExample {

  val scatterCanvas = new TileSpace(Some("example"))()
  scatterCanvas.tiles.add(StringTile(scatterCanvas, "Hello world"))
  scatterCanvas.tiles.add(new PlayTile(scatterCanvas))


  def page:VNode = Common.layout(<.div(
    scatterCanvas
  ))

}
