package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful
import veautiful.{Blueprint, DiffNode, VNode, DiffComponent}

import org.scalajs.dom
import dom.html


/** The namespace for HTML nodes */
val NS = "http://www.w3.org/1999/xhtml"

/** For convenience, we also keep a reference to the SVG NS in this package */
def svgNS = veautiful.svg.NS
def htmlNS = NS



type VHtmlNode = VNode[dom.Node]

type VHtmlDiffNode = DiffNode[dom.Element, dom.Node]

/** A DiffComponent producing HTML */
type DHtmlComponent = DiffComponent[dom.html.Element, dom.Node]

/** A DElement for HTML */
type DHtmlElement = DElement[html.Element]

/** A Blueprint for an HTML DElement */
type DHtmlBlueprint = Blueprint[DHtmlElement]

/** Either DHtmlElement or a blueprint for it */
type DHtmlContent = DHtmlElement | DHtmlBlueprint

/** Can be passed into a DHtmlElement's apply method */
type DHtmlModifier = ElementChild[html.Element]

/** For convenience, we alias the DSvgElement type into the html package as well */
type DSvgElement = veautiful.svg.DSvgElement

/** For convenience, aliased in from the dom package */
type DDomElement = veautiful.dom.DDomElement

/** For convenience, aliased in from the dom package */
type DDomComponent = veautiful.dom.DDomComponent


/** For convenience, we alias the VSVGModifier type into the html package as well */
type DSvgModifier = veautiful.svg.DSvgModifier

@deprecated("This has been renamed to DDomComponent since 0.3-M2. Or use DHtmlComponent if you produce DHtmlContent")
type VHtmlComponent = DDomComponent

@deprecated("This has been renamed to DHtmlModifier since 0.3-M2")
type HTMLAppliable = DHtmlModifier

@deprecated("This has been renamed to DHtmlElement since 0.3-M2")
type VHTMLElement = DHtmlElement

object HTML extends DElementBuilder[dom.HTMLElement]("html", NS) {

  def p = applyT[html.Paragraph]("p")
  def div = applyT[html.Div]("div")
  def img = applyT[html.Image]("img")
  def a = applyT[html.Anchor]("a")
  def span = applyT[html.Span]("span")
  def h1 = applyT[html.Heading]("h1")
  def h2 = applyT[html.Heading]("h2")
  def h3 = applyT[html.Heading]("h3")
  def h4 = applyT[html.Heading]("h4")
  def h5 = applyT[html.Heading]("h5")
  def h6 = applyT[html.Heading]("h6")

  def iframe = applyT[html.IFrame]("iframe")
  def pre = applyT[html.Pre]("pre")
  def br = applyT[html.BR]("br")
  def canvas = applyT[html.Canvas]("canvas")
  def form = applyT[html.Form]("form")

  def button = applyT[html.Button]("button")
  def input = applyT[html.Input]("input")
  def textarea = applyT[html.TextArea]("textarea")

  def ol = applyT[html.OList]("ol")
  def ul = applyT[html.UList]("ul")
  def li = applyT[html.LI]("li")

  def table = applyT[html.Table]("table")
  def thead = apply("thead")
  def tbody = apply("tbody")
  def tr = applyT[html.TableRow]("tr")
  def th = applyT[html.TableCell]("th")
  def td = applyT[html.TableCell]("td")

  def svg = veautiful.svg.svg
  def SVG = veautiful.svg.SVG

}

val el = HTML
val < = HTML
export HTML.*