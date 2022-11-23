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

type VHtmlElement = VNode[dom.html.Element]

type VHtmlDiffNode = DiffNode[dom.Element, dom.Node]

/** A DiffComponent producing HTML */
type DHtmlComponent = DiffComponent[dom.html.Element, dom.Node]

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

/** For convenience, aliased in from the dom package */
type DDomElement = veautiful.dom.DDomElement

/** For convenience, aliased in from the svg package */
type DSvgComponent = veautiful.svg.DSvgComponent

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

  def a = applyT[dom.html.Anchor]("a")
  def abbr = applyT[dom.html.Anchor]("abbr")
  def audio = applyT[dom.html.Audio]("audio")
  def area = applyT[dom.html.Area]("area")
  def article = applyT[dom.html.Element]("article")
  def aside = applyT[dom.html.Element]("aside")
  def b = applyT[dom.html.Element]("b")
  def base = applyT[dom.html.Base]("base")
  def bdi = applyT[dom.html.Element]("bdi")
  def bdo = applyT[dom.html.Element]("bdo")
  def blockquote = applyT[dom.html.Quote]("blockquote")
  def body = applyT[dom.html.Body]("body")
  def br = applyT[dom.html.BR]("br")
  def button = applyT[dom.html.Button]("button")
  def canvas = applyT[dom.html.Canvas]("canvas")
  def caption = applyT[dom.html.TableCaption]("caption")
  def cite = applyT[dom.html.Element]("cite")
  def code = applyT[dom.html.Element]("code")
  def col = applyT[dom.html.TableCol]("col")
  def colgroup = applyT[dom.html.TableCol]("colgroup")
  def data = applyT[dom.html.Element]("data") // need a Data type, but there's only DataList
  def datalist = applyT[dom.html.DataList]("datalist") 
  def dd = applyT[dom.html.Element]("dd")
  def del = applyT[dom.html.Mod]("del")
  def details = applyT[dom.html.Element]("details") // need Details type
  def dfn = applyT[dom.html.Element]("dfn")
  def dialog = applyT[dom.html.Element]("dialog") // need Dialog type
  def div = applyT[dom.html.Div]("div")
  def dl = applyT[dom.html.DList]("dl")
  def dt = applyT[dom.html.Element]("dt")
  def em = applyT[dom.html.Element]("em")
  def embed = applyT[dom.html.Embed]("embed")
  def fieldset = applyT[dom.html.FieldSet]("fieldset")
  def figcaption = applyT[dom.html.Element]("figcaption")
  def figure = applyT[dom.html.Element]("figure")
  def footer = applyT[dom.html.Element]("footer")
  def form = applyT[dom.html.Form]("form")
  def h1 = applyT[dom.html.Heading]("h1")
  def h2 = applyT[dom.html.Heading]("h2")
  def h3 = applyT[dom.html.Heading]("h3")
  def h4 = applyT[dom.html.Heading]("h4")
  def h5 = applyT[dom.html.Heading]("h5")
  def h6 = applyT[dom.html.Heading]("h6")
  def head = applyT[dom.html.Head]("head")
  def header = applyT[dom.html.Element]("header")
  def hgroup = applyT[dom.html.Element]("hgroup")
  def hr = applyT[dom.html.HR]("hr")
  def html = applyT[dom.html.Html]("html")
  def i = applyT[dom.html.Element]("i")
  def iframe = applyT[dom.html.IFrame]("iframe")
  def img = applyT[dom.html.Image]("img")
  def input = applyT[dom.html.Input]("input")
  def ins = applyT[dom.html.Mod]("ins")
  def kbd = applyT[dom.html.Element]("kbd")
  def label = applyT[dom.html.Label]("label")
  def legend = applyT[dom.html.Legend]("legend")
  def li = applyT[dom.html.LI]("li")
  def link = applyT[dom.html.Link]("link")
  def main = applyT[dom.html.Element]("main")
  def map = applyT[dom.html.Map]("map")
  def mark = applyT[dom.html.Element]("mark")
  def menu = applyT[dom.html.Menu]("menu")
  def meta = applyT[dom.html.Meta]("meta")
  def meter = applyT[dom.html.Element]("meter") // Need a Meter type
  def noscript = applyT[dom.html.Element]("noscript") 
  def `object` = applyT[dom.html.Object]("object") // Need a Meter type
  def Object = `object`
  def ol = applyT[dom.html.OList]("ol")
  def optgroup = applyT[dom.html.OptGroup]("optgroup")
  def option = applyT[dom.html.Option]("option")
  def output = applyT[dom.html.Element]("output") // Need an Output type
  def p = applyT[dom.html.Paragraph]("p")
  def picture = applyT[dom.html.Element]("picture") // Need a Picture type
  def pre = applyT[dom.html.Pre]("pre")
  def progress = applyT[dom.html.Progress]("progress")
  def q = applyT[dom.html.Quote]("q")
  def rp = applyT[dom.html.Element]("rp")
  def rt = applyT[dom.html.Element]("rt")
  def ruby = applyT[dom.html.Element]("ruby")
  def s = applyT[dom.html.Element]("s")
  def samp = applyT[dom.html.Element]("samp")
  def script = applyT[dom.html.Script]("script")
  def section = applyT[dom.html.Element]("section")
  def select = applyT[dom.html.Select]("select")
  def slot = applyT[dom.html.Element]("slot") // Need a Slot type
  def small = applyT[dom.html.Element]("small")
  def source = applyT[dom.html.Source]("source")
  def span = applyT[dom.html.Span]("span")
  def strong = applyT[dom.html.Element]("strong")
  def style = applyT[dom.html.Style]("style")
  def sub = applyT[dom.html.Element]("sub")
  def summary = applyT[dom.html.Element]("summary")
  def sup = applyT[dom.html.Element]("sup")
  def table = applyT[dom.html.Table]("table")
  def tbody = applyT[dom.html.TableSection]("tbody")
  def td = applyT[dom.html.TableCell]("td")
  def template = applyT[dom.html.Element]("template") // Need a Template type
  def textarea = applyT[dom.html.TextArea]("textarea")
  def tfoot = applyT[dom.html.TableSection]("tfoot")
  def th = applyT[dom.html.TableCell]("th")
  def thead = applyT[dom.html.TableSection]("thead")
  def time = applyT[dom.html.Element]("time") // Need a Time type
  def title = applyT[dom.html.Title]("title")
  def tr = applyT[dom.html.TableRow]("tr")
  def track = applyT[dom.html.Track]("track")
  def u = applyT[dom.html.Element]("u")
  def ul = applyT[dom.html.UList]("ul")
  def `var` = applyT[dom.html.Element]("var")
  def Var = `var`
  def video = applyT[dom.html.Video]("video")
  def wbr = applyT[dom.html.Element]("wbr")

  def svg = veautiful.svg.svg
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