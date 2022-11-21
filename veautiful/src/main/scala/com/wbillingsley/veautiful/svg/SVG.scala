package com.wbillingsley.veautiful.svg

import com.wbillingsley.veautiful.html
import org.scalajs.dom
import org.scalajs.dom.{Element, Event, Node}
import com.wbillingsley.veautiful.html.DElementBuilder

type VSVGElement = html.DElement[dom.svg.Element]
type SVGAppliable = html.ElementChild[dom.svg.Element]
type VSVGModifier = SVGAppliable


object SVG extends DElementBuilder[dom.SVGElement]("svg", html.DElement.svgNS) {

  def svg = applyT[dom.svg.SVG]("svg")

  def circle = applyT[dom.svg.Circle]("circle")

  def ellipse = applyT[dom.svg.Ellipse]("ellipse")

  def polygon = applyT[dom.svg.Polygon]("polygon")

  def line = applyT[dom.svg.Line]("line")

  def text = applyT[dom.svg.Text]("text")

  def tspan = applyT[dom.svg.TSpan]("tspan")

  def g = applyT[dom.svg.G]("g")

  def path = applyT[dom.svg.Path]("path")

  def rect = applyT[dom.svg.Element]("rect")

  def foreignObject = applyT[dom.svg.Element]("foreignObject")

}

val < = SVG
val el = SVG

export el.*