package com.wbillingsley.veautiful.svg

import com.wbillingsley.veautiful
import veautiful.html
import org.scalajs.dom
import org.scalajs.dom.{Element, Event, Node}
import html.{DElementBuilder, ModifierDSL}

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

object SVG extends DElementBuilder[dom.SVGElement]("svg", NS) {

  def a = applyT[dom.svg.A]("a")

  def circle = applyT[dom.svg.Circle]("circle")

  def clipPath = applyT[dom.svg.ClipPath]("clipPath")

  def defs = applyT[dom.svg.Defs]("defs")

  def desc = applyT[dom.svg.Desc]("desc")

  def ellipse = applyT[dom.svg.Ellipse]("ellipse")

  def feBlend = applyT[dom.svg.FEBlend]("feBlend")
  def feColorMatrix = applyT[dom.svg.FEColorMatrix]("feColorMatrix")
  def feComponentTransfer = applyT[dom.svg.FEComponentTransfer]("feComponentTransfer")
  def feConvolveMatrix = applyT[dom.svg.FEConvolveMatrix]("feConvolveMatrix")
  def feDiffuseLighting = applyT[dom.svg.FEDiffuseLighting]("feDiffuseLighting")
  def feDisplacementMap = applyT[dom.svg.FEDisplacementMap]("feDisplacementMap")
  def feFlood = applyT[dom.svg.FEFlood]("feFlood")
  def feFuncA = applyT[dom.svg.FEFuncA]("feFuncA")
  def feFuncB = applyT[dom.svg.FEFuncB]("feFuncB")
  def feFuncG = applyT[dom.svg.FEFuncG]("feFuncG")
  def feFuncR = applyT[dom.svg.FEFuncR]("feFuncR")
  def feGaussianBlur = applyT[dom.svg.FEGaussianBlur]("feGaussianBlur")
  def feImage = applyT[dom.svg.FEImage]("feImage")
  def feMerge = applyT[dom.svg.FEMerge]("feMerge")
  def feMergeNode = applyT[dom.svg.FEMergeNode]("feMergeNode")
  def feMorphology = applyT[dom.svg.FEMorphology]("feMorphology")
  def feOffset = applyT[dom.svg.FEOffset]("feOffset")
  def fePointLight = applyT[dom.svg.FEPointLight]("fePointLight")
  def feSpecularLighting = applyT[dom.svg.FESpecularLighting]("feSpecularLighting")
  def feSpotlight = applyT[dom.svg.FESpotLight]("feSpotlight")
  def feTile = applyT[dom.svg.FETile]("feTile")
  def feTurbulence = applyT[dom.svg.FETurbulence]("feTurbulence")
  
  def filter = applyT[dom.svg.Filter]("filter")

  def foreignObject = applyT[dom.svg.Element]("foreignObject") // need ForeignObject type?

  def g = applyT[dom.svg.G]("g")
  
  def image = applyT[dom.svg.Image]("image")

  def line = applyT[dom.svg.Line]("line")

  def linearGradient = applyT[dom.svg.LinearGradient]("linearGradient")

  def marker = applyT[dom.svg.Marker]("marker")

  def mask = applyT[dom.svg.Mask]("mask")

  def metadata = applyT[dom.svg.Metadata]("metadata")

  def path = applyT[dom.svg.Path]("path")

  def pattern = applyT[dom.svg.Pattern]("pattern")

  def polygon = applyT[dom.svg.Polygon]("polygon")

  def polyline = applyT[dom.svg.Polyline]("polyline")

  def radialGradient = applyT[dom.svg.RadialGradient]("radialGradient")

  def rect = applyT[dom.svg.Element]("rect")

  def script = applyT[dom.svg.Script]("script")

  def stop = applyT[dom.svg.Stop]("stop")

  def style = applyT[dom.svg.Style]("style")

  def svg = applyT[dom.svg.SVG]("svg")

  def switch = applyT[dom.svg.Switch]("switch")

  def symbol = applyT[dom.svg.Symbol]("symbol")

  def text = applyT[dom.svg.Text]("text")

  def textPath = applyT[dom.svg.TextPath]("textPath")

  def title = applyT[dom.svg.Title]("title")

  def tspan = applyT[dom.svg.TSpan]("tspan")

  def use = applyT[dom.svg.Use]("use")

  def view = applyT[dom.svg.View]("view")
}

val < = SVG
val el = SVG

export el.*


object SvgModifiers extends ModifierDSL {

}

val ^ = SvgModifiers
val modifiers = SvgModifiers