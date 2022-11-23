package com.wbillingsley.wren

import com.wbillingsley.veautiful.html.{<, DSvgComponent, ^}

case class Circuit(components:Seq[Component], width:Int, height:Int) extends DSvgComponent {

  override protected def render = {
    <.svg(^.attr("width") := width, ^.attr("height") := height, ^.cls := "wren-canvas",
      components.map(_.render),
      components.flatMap(_.terminals).map(_.render)
    )
  }

}
