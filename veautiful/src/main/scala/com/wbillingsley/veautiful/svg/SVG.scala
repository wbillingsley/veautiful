package com.wbillingsley.veautiful.svg

import com.wbillingsley.veautiful
import veautiful.html
import org.scalajs.dom
import org.scalajs.dom.{Element, Event, Node}
import html.DElementBuilder

/** A DElement for SVG */
type DSvgElement = html.DElement[dom.svg.Element]

/** Can be passed to a DSvgComponent's apply method */
type DSvgModifier = html.ElementChild[dom.svg.Element]

/** A DiffComponent producing DSvgContent */
type DSvgComponent = veautiful.DiffComponent[dom.svg.Element, dom.Node]

/** A Blueprint for a DSvgElement */
type DSvgBlueprint = veautiful.Blueprint[DSvgElement]

/** A DSvgElement or a Blueprint for it */ 
type DSvgContent = DSvgElement | DSvgBlueprint

/** The namespace for SVG nodes */
val NS = "http://www.w3.org/2000/svg"

object SVG extends DElementBuilder[dom.SVGElement]("svg", NS) {

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