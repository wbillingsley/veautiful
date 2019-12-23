package com.wbillingsley.wren

import com.wbillingsley.veautiful.html.{VHtmlComponent, VHtmlDiffNode}
import com.wbillingsley.veautiful.{<, DiffComponent, DiffNode, SVG, VNode, ^}
import org.scalajs.dom.Node

case class Circuit(components:Seq[Component], width:Int, height:Int) extends VHtmlComponent {

  override protected def render: VHtmlDiffNode = {
    <.svg(^.attr("width") := width, ^.attr("height") := height, ^.cls := "wren-canvas",
      components.map(_.render) ++ components.flatMap(_.terminals).map(_.render)
    )
  }

}
