package com.wbillingsley.wren

import com.wbillingsley.veautiful.{<, DiffComponent, DiffNode, SVG, VNode, ^}
import org.scalajs.dom.Node

case class Circuit(components:Seq[Component], width:Int, height:Int) extends DiffComponent {

  override protected def render: DiffNode = {
    <.svg(^.attr("width") := width, ^.attr("height") := height, ^.cls := "wren-canvas",
      components.map(_.render)
    )
  }

}
