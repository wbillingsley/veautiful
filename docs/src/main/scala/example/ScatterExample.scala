package example

import com.wbillingsley.scatter.jstiles.{PlayTile, StringTile}
import com.wbillingsley.scatter.TileSpace
import com.wbillingsley.veautiful.{<, VNode}

object ScatterExample {

  val scatterCanvas = new TileSpace(Some("example"))()
  scatterCanvas.tiles.append(
    //StringTile(scatterCanvas, "Hello world"),
    new PlayTile(scatterCanvas)
  )


  def page:VNode = Common.layout(<.div(
    scatterCanvas
  ))

}
