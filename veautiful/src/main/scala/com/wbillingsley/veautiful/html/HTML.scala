package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful
import veautiful.{Blueprint, DiffNode, VNode, DiffComponent}
import org.scalajs.dom 

/** The namespace for HTML nodes */
val NS = "http://www.w3.org/1999/xhtml"

/** For convenience, we also keep a reference to the SVG NS in this package */
def svgNS = veautiful.svg.NS
def htmlNS = NS



type VDomNode = veautiful.dom.VDomNode

type VDomElement = veautiful.dom.VDomElement

type VDomContent = veautiful.dom.VDomContent

type VHtmlElement = VNode[dom.html.Element]

type VHtmlBlueprint = Blueprint[VHtmlElement]

type VHtmlContent = VHtmlElement | VHtmlBlueprint

type VHtmlDiffNode = DiffNode[dom.Element, dom.Node]

/** A DiffComponent producing HTML */
type DHtmlComponent = DomDiffComponent[dom.html.Element]

/** A DElement for HTML */
type DHtmlElement = DElement[dom.html.Element]

/** A Blueprint for an HTML DElement */
type DHtmlBlueprint = Blueprint[DHtmlElement]

/** Either DHtmlElement or a blueprint for it */
type DHtmlContent = DHtmlElement | DHtmlBlueprint

/** Can be passed into a DHtmlElement's apply method */
type DHtmlModifier = ElementChild[dom.html.Element]

/** For convenience, we alias the DSvgElement type into the html package as well */
type DSvgElement = veautiful.svg.DSvgElement

/** For convenience, we alias the DSvgElement type into the html package as well */
type DSvgContent = veautiful.svg.DSvgContent

/** For convenience, aliased in from the dom package */
type DDomElement = veautiful.dom.DDomElement

/** For convenience, aliased in from the dom package */
type DDomContent = veautiful.dom.DDomContent

/** For convenience, aliased in from the svg package */
type DSvgComponent = veautiful.svg.DSvgComponent

/** For convenience, aliased in from the dom package */
type DDomComponent = veautiful.dom.DDomComponent

/** For convenience, we alias the VSVGModifier type into the html package as well */
type DSvgModifier = veautiful.svg.DSvgModifier

/** For convenience, we alias the VSVGModifier type into the html package as well */
type DDomModifier = veautiful.dom.DDomModifier


@deprecated("This has been renamed to DDomComponent since 0.3-M2. Or use DHtmlComponent if you produce DHtmlContent")
type VHtmlComponent = DDomComponent

@deprecated("This has been renamed to DHtmlModifier since 0.3-M2")
type HTMLAppliable = DHtmlModifier

@deprecated("This has been renamed to DHtmlElement since 0.3-M2")
type VHTMLElement = DHtmlElement

/**
 * Gives us a method for creating C[T]s for subtypes of DOM elements.
 * Used by the HTML and SVG DSLs so that we can use the same DSL to build
 * DElementBlueprint[T]s or DynamicElementBlueprint[T]s or any other such type.
 */
trait DSLFactory[C[_ <: Base], Base <: dom.Element] {
  def applyT[T <: Base](name:String):C[T]
}

/**
 * Provides a DSL for constructing objects reprsenting HTML elements
 */
trait HTMLDSL[C[_ <: dom.html.Element]](factory: DSLFactory[C, dom.html.Element]) {

  def a = factory.applyT[dom.html.Anchor]("a")
  def abbr = factory.applyT[dom.html.Anchor]("abbr")
  def audio = factory.applyT[dom.html.Audio]("audio")
  def area = factory.applyT[dom.html.Area]("area")
  def article = factory.applyT[dom.html.Element]("article")
  def aside = factory.applyT[dom.html.Element]("aside")
  def b = factory.applyT[dom.html.Element]("b")
  def base = factory.applyT[dom.html.Base]("base")
  def bdi = factory.applyT[dom.html.Element]("bdi")
  def bdo = factory.applyT[dom.html.Element]("bdo")
  def blockquote = factory.applyT[dom.html.Quote]("blockquote")
  def body = factory.applyT[dom.html.Body]("body")
  def br = factory.applyT[dom.html.BR]("br")
  def button = factory.applyT[dom.html.Button]("button")
  def canvas = factory.applyT[dom.html.Canvas]("canvas")
  def caption = factory.applyT[dom.html.TableCaption]("caption")
  def cite = factory.applyT[dom.html.Element]("cite")
  def code = factory.applyT[dom.html.Element]("code")
  def col = factory.applyT[dom.html.TableCol]("col")
  def colgroup = factory.applyT[dom.html.TableCol]("colgroup")
  def data = factory.applyT[dom.html.Element]("data") // need a Data type, but there's only DataList
  def datalist = factory.applyT[dom.html.DataList]("datalist") 
  def dd = factory.applyT[dom.html.Element]("dd")
  def del = factory.applyT[dom.html.Mod]("del")
  def details = factory.applyT[dom.html.Element]("details") // need Details type
  def dfn = factory.applyT[dom.html.Element]("dfn")
  def dialog = factory.applyT[dom.html.Element]("dialog") // need Dialog type
  def div = factory.applyT[dom.html.Div]("div")
  def dl = factory.applyT[dom.html.DList]("dl")
  def dt = factory.applyT[dom.html.Element]("dt")
  def em = factory.applyT[dom.html.Element]("em")
  def embed = factory.applyT[dom.html.Embed]("embed")
  def fieldset = factory.applyT[dom.html.FieldSet]("fieldset")
  def figcaption =factory.applyT[dom.html.Element]("figcaption")
  def figure = factory.applyT[dom.html.Element]("figure")
  def footer = factory.applyT[dom.html.Element]("footer")
  def form = factory.applyT[dom.html.Form]("form")
  def h1 = factory.applyT[dom.html.Heading]("h1")
  def h2 = factory.applyT[dom.html.Heading]("h2")
  def h3 = factory.applyT[dom.html.Heading]("h3")
  def h4 = factory.applyT[dom.html.Heading]("h4")
  def h5 = factory.applyT[dom.html.Heading]("h5")
  def h6 = factory.applyT[dom.html.Heading]("h6")
  def head = factory.applyT[dom.html.Head]("head")
  def header = factory.applyT[dom.html.Element]("header")
  def hgroup = factory.applyT[dom.html.Element]("hgroup")
  def hr = factory.applyT[dom.html.HR]("hr")
  def html = factory.applyT[dom.html.Html]("html")
  def i = factory.applyT[dom.html.Element]("i")
  def iframe = factory.applyT[dom.html.IFrame]("iframe")
  def img = factory.applyT[dom.html.Image]("img")
  def input = factory.applyT[dom.html.Input]("input")
  def ins = factory.applyT[dom.html.Mod]("ins")
  def kbd = factory.applyT[dom.html.Element]("kbd")
  def label = factory.applyT[dom.html.Label]("label")
  def legend = factory.applyT[dom.html.Legend]("legend")
  def li = factory.applyT[dom.html.LI]("li")
  def link = factory.applyT[dom.html.Link]("link")
  def main = factory.applyT[dom.html.Element]("main")
  def map = factory.applyT[dom.html.Map]("map")
  def mark = factory.applyT[dom.html.Element]("mark")
  def menu = factory.applyT[dom.html.Menu]("menu")
  def meta = factory.applyT[dom.html.Meta]("meta")
  def meter = factory.applyT[dom.html.Element]("meter") // Need a Meter type
  def noscript = factory.applyT[dom.html.Element]("noscript") 
  def `object` = factory.applyT[dom.html.Object]("object") // Need a Meter type
  def Object = `object`
  def ol = factory.applyT[dom.html.OList]("ol")
  def optgroup = factory.applyT[dom.html.OptGroup]("optgroup")
  def option = factory.applyT[dom.html.Option]("option")
  def output = factory.applyT[dom.html.Element]("output") // Need an Output type
  def p = factory.applyT[dom.html.Paragraph]("p")
  def picture = factory.applyT[dom.html.Element]("picture") // Need a Picture type
  def pre = factory.applyT[dom.html.Pre]("pre")
  def progress = factory.applyT[dom.html.Progress]("progress")
  def q = factory.applyT[dom.html.Quote]("q")
  def rp = factory.applyT[dom.html.Element]("rp")
  def rt = factory.applyT[dom.html.Element]("rt")
  def ruby = factory.applyT[dom.html.Element]("ruby")
  def s = factory.applyT[dom.html.Element]("s")
  def samp = factory.applyT[dom.html.Element]("samp")
  def script = factory.applyT[dom.html.Script]("script")
  def section = factory.applyT[dom.html.Element]("section")
  def select = factory.applyT[dom.html.Select]("select")
  def slot = factory.applyT[dom.html.Element]("slot") // Need a Slot type
  def small = factory.applyT[dom.html.Element]("small")
  def source = factory.applyT[dom.html.Source]("source")
  def span = factory.applyT[dom.html.Span]("span")
  def strong = factory.applyT[dom.html.Element]("strong")
  def style = factory.applyT[dom.html.Style]("style")
  def sub = factory.applyT[dom.html.Element]("sub")
  def summary = factory.applyT[dom.html.Element]("summary")
  def sup = factory.applyT[dom.html.Element]("sup")
  def table = factory.applyT[dom.html.Table]("table")
  def tbody = factory.applyT[dom.html.TableSection]("tbody")
  def td = factory.applyT[dom.html.TableCell]("td")
  def template = factory.applyT[dom.html.Element]("template") // Need a Template type
  def textarea = factory.applyT[dom.html.TextArea]("textarea")
  def tfoot = factory.applyT[dom.html.TableSection]("tfoot")
  def th = factory.applyT[dom.html.TableCell]("th")
  def thead = factory.applyT[dom.html.TableSection]("thead")
  def time = factory.applyT[dom.html.Element]("time") // Need a Time type
  def title = factory.applyT[dom.html.Title]("title")
  def tr = factory.applyT[dom.html.TableRow]("tr")
  def track = factory.applyT[dom.html.Track]("track")
  def u = factory.applyT[dom.html.Element]("u")
  def ul = factory.applyT[dom.html.UList]("ul")
  def `var` = factory.applyT[dom.html.Element]("var")
  def Var = `var`
  def video = factory.applyT[dom.html.Video]("video")
  def wbr = factory.applyT[dom.html.Element]("wbr")

}

val htmlDElementBuilder = DBlueprintBuilder[dom.html.Element]("html", NS) 

object HTML extends DBlueprintBuilder[dom.HTMLElement]("html", NS) with HTMLDSL(htmlDElementBuilder) {
  def svg = veautiful.svg.svg

  object dynamic extends HTMLDSL(DEBlueprintBuilder[dom.html.Element](NS))

  object mutable extends HTMLDSL(DElementBuilder[dom.html.Element]("html", NS))
}

val el = HTML
val < = HTML

export HTML.*

def SVG = veautiful.svg.SVG

object HtmlModifiers extends ModifierDSL {

  def onBeforeInput = Lsntrable[dom.InputEvent]("beforeinput")
  def onInput = Lsntrable[dom.InputEvent]("input")
  def onChange = Lsntrable[dom.InputEvent]("change")


}

val ^ = HtmlModifiers
val modifiers = HtmlModifiers