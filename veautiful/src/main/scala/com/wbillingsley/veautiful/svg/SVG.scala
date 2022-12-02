package com.wbillingsley.veautiful.svg

import com.wbillingsley.veautiful
import veautiful.html
import org.scalajs.dom
import org.scalajs.dom.{Element, Event, Node}
import html.{DElementBuilder, ModifierDSL, DSLFactory, DEBlueprintBuilder}

/** A DElement for SVG */
type DSvgElement = html.DElement[dom.svg.Element]

/** Can be passed to a DSvgComponent's apply method */
type DSvgModifier = html.ElementChild[dom.svg.Element]

/** A DiffComponent producing DSvgContent */
type DSvgComponent = veautiful.html.DomDiffComponent[dom.svg.Element]

/** A Blueprint for a DSvgElement */
type DSvgBlueprint = veautiful.Blueprint[DSvgElement]

/** A DSvgElement or a Blueprint for it */ 
type DSvgContent = DSvgElement | DSvgBlueprint

/** The namespace for SVG nodes */
val NS = "http://www.w3.org/2000/svg"

trait SVGDSL[C[_ <: dom.svg.Element]](factory: DSLFactory[C, dom.svg.Element]) {

  def a = factory.applyT[dom.svg.A]("a")

  def circle = factory.applyT[dom.svg.Circle]("circle")

  def clipPath = factory.applyT[dom.svg.ClipPath]("clipPath")

  def defs = factory.applyT[dom.svg.Defs]("defs")

  def desc = factory.applyT[dom.svg.Desc]("desc")

  def ellipse = factory.applyT[dom.svg.Ellipse]("ellipse")

  def feBlend = factory.applyT[dom.svg.FEBlend]("feBlend")
  def feColorMatrix = factory.applyT[dom.svg.FEColorMatrix]("feColorMatrix")
  def feComponentTransfer = factory.applyT[dom.svg.FEComponentTransfer]("feComponentTransfer")
  def feConvolveMatrix = factory.applyT[dom.svg.FEConvolveMatrix]("feConvolveMatrix")
  def feDiffuseLighting = factory.applyT[dom.svg.FEDiffuseLighting]("feDiffuseLighting")
  def feDisplacementMap = factory.applyT[dom.svg.FEDisplacementMap]("feDisplacementMap")
  def feFlood = factory.applyT[dom.svg.FEFlood]("feFlood")
  def feFuncA = factory.applyT[dom.svg.FEFuncA]("feFuncA")
  def feFuncB = factory.applyT[dom.svg.FEFuncB]("feFuncB")
  def feFuncG = factory.applyT[dom.svg.FEFuncG]("feFuncG")
  def feFuncR = factory.applyT[dom.svg.FEFuncR]("feFuncR")
  def feGaussianBlur = factory.applyT[dom.svg.FEGaussianBlur]("feGaussianBlur")
  def feImage = factory.applyT[dom.svg.FEImage]("feImage")
  def feMerge = factory.applyT[dom.svg.FEMerge]("feMerge")
  def feMergeNode = factory.applyT[dom.svg.FEMergeNode]("feMergeNode")
  def feMorphology = factory.applyT[dom.svg.FEMorphology]("feMorphology")
  def feOffset = factory.applyT[dom.svg.FEOffset]("feOffset")
  def fePointLight = factory.applyT[dom.svg.FEPointLight]("fePointLight")
  def feSpecularLighting = factory.applyT[dom.svg.FESpecularLighting]("feSpecularLighting")
  def feSpotlight = factory.applyT[dom.svg.FESpotLight]("feSpotlight")
  def feTile = factory.applyT[dom.svg.FETile]("feTile")
  def feTurbulence = factory.applyT[dom.svg.FETurbulence]("feTurbulence")
  
  def filter = factory.applyT[dom.svg.Filter]("filter")

  def foreignObject = factory.applyT[dom.svg.Element]("foreignObject") // need ForeignObject type?

  def g = factory.applyT[dom.svg.G]("g")
  
  def image = factory.applyT[dom.svg.Image]("image")

  def line = factory.applyT[dom.svg.Line]("line")

  def linearGradient = factory.applyT[dom.svg.LinearGradient]("linearGradient")

  def marker = factory.applyT[dom.svg.Marker]("marker")

  def mask = factory.applyT[dom.svg.Mask]("mask")

  def metadata = factory.applyT[dom.svg.Metadata]("metadata")

  def path = factory.applyT[dom.svg.Path]("path")

  def pattern = factory.applyT[dom.svg.Pattern]("pattern")

  def polygon = factory.applyT[dom.svg.Polygon]("polygon")

  def polyline = factory.applyT[dom.svg.Polyline]("polyline")

  def radialGradient = factory.applyT[dom.svg.RadialGradient]("radialGradient")

  def rect = factory.applyT[dom.svg.Element]("rect")

  def script = factory.applyT[dom.svg.Script]("script")

  def stop = factory.applyT[dom.svg.Stop]("stop")

  def style = factory.applyT[dom.svg.Style]("style")

  def svg = factory.applyT[dom.svg.SVG]("svg")

  def switch = factory.applyT[dom.svg.Switch]("switch")

  def symbol = factory.applyT[dom.svg.Symbol]("symbol")

  def text = factory.applyT[dom.svg.Text]("text")

  def textPath = factory.applyT[dom.svg.TextPath]("textPath")

  def title = factory.applyT[dom.svg.Title]("title")

  def tspan = factory.applyT[dom.svg.TSpan]("tspan")

  def use = factory.applyT[dom.svg.Use]("use")

  def view = factory.applyT[dom.svg.View]("view")
}

val svgDElementBuilder = DElementBuilder[dom.svg.Element]("html", NS) 

object SVG extends DElementBuilder[dom.svg.Element]("svg", NS) with SVGDSL(svgDElementBuilder) {

  object dynamic extends SVGDSL(DEBlueprintBuilder[dom.svg.Element](NS))
}


val < = SVG
val el = SVG

export el.*


object SvgModifiers extends ModifierDSL {

}

val ^ = SvgModifiers
val modifiers = SvgModifiers