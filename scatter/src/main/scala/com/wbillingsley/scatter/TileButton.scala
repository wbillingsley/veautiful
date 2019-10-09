package com.wbillingsley.scatter
import com.wbillingsley.veautiful.{<, DiffNode, SVG, ^}

class TileButton(text:String, action: => Unit, cls:String = "btn btn-primary") extends TileComponent {

  override def emptySockets: Seq[(Int, Int, Socket)] = Seq.empty

  val button = <.button(text, ^.cls := cls, ^.onClick --> action)

  var width:Int = 20
  var height:Int = 20

  override protected def render: DiffNode = SVG.g(
    SVG.foreignObject(^.attr("width") := width, ^.attr("height") := height,
      button
    )
  )

  override def size:Option[(Int, Int)] = button.domNode.map(TileComponent.sizeOf)

  override def layoutChildren(): Unit = {
    super.layoutChildren()
    for { (w, h) <- size } {
      width = w
      height = h
      rerender()
    }
  }


}
