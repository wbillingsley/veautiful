package example

import com.wbillingsley.scatter.{TextTile, TileSpace}
import com.wbillingsley.veautiful.{<, VNode}

object ScatterExample {

  val scatterCanvas = new TileSpace(Some("example"))()
  scatterCanvas.tiles.add(TextTile("Hello world"))

  def page:VNode = Common.layout(<.div(
    scatterCanvas
  ))

}
