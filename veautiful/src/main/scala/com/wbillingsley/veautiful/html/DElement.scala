package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.html.<.DEAAction
import com.wbillingsley.veautiful.reconcilers.Reconciler
import com.wbillingsley.veautiful.{DefaultNodeOps, DiffNode, NodeOps, VNode}
import org.scalajs.dom
import org.scalajs.dom.{Element, Event, Node, html}
import html.Div

import scala.scalajs.js

case class Lstnr(`type`:String, func:Event => _, usCapture:Boolean=false)
case class AttrVal(name:String, value:String)
case class PropVal(name:String, value:js.Any)
case class InlineStyle(name:String, value:String)
case class EvtListener[T](`type`:String, f:Function[T, _], capture:Boolean)


object DElement {
  val htmlNS = "http://www.w3.org/1999/xhtml"
  val svgNS = "http://www.w3.org/2000/svg"
}

case class DElement[+T <: dom.Element](name:String, var uniqEl:Option[Any] = None, ns:String = DElement.htmlNS) extends DiffNode[T, dom.Node] {

  var attributes:Map[String, AttrVal] = Map.empty

  var properties:Map[String, PropVal] = Map.empty

  var listeners:Map[String, Lstnr] = Map.empty

  var styles:Seq[InlineStyle] = Seq.empty

  var reconciler:Reconciler = Reconciler.default

  private[this] var _domNode:Option[T] = None

  def domNode = _domNode

  override def attachSelf():T = {
    val n = create()
    _domNode = Some(n)
    n
  }

  override def detachSelf():Unit = {
    _domNode = None
  }

  override def key: Option[Any] = uniqEl

  /**
    * In DOM Events, it is very difficult to de-register an anonymous listener.
    * And in our code, it is very easy to end up with an event listener being an anonymous
    * function.
    * So, in order to make changing event listeners practical, instead of registering the
    * event listener, we always register this dispatcher function as the event listener.
    */
  val eventDispatch:(Event) => Unit = (e:Event) => {
    for {
      h <- listeners.get(e.`type`)
    } h.func.apply(e)
  }

  def updateSelf: PartialFunction[DiffNode[_, Node], _] = {
    case el:DElement[T] =>

      // Update attributes
      for { n <- domNode } {

        for {
          (k, _) <- attributes if !el.attributes.contains(k)
        } n.removeAttribute(k)

        for {
          (k, v) <- el.attributes if !attributes.get(k).contains(v)
        } n.setAttribute(v.name, v.value)

        attributes = el.attributes
      }

      // Update properties
      properties = el.properties
      applyPropsToNode(properties.values)

      removeStylesFromNode(styles)
      styles = el.styles
      applyStylesToNode(styles)

      if (listeners.keys != el.listeners.keys) {
        println("Listeners differ")
        removeLsntrsFromNode(listeners.values)
        applyLsntrsToNode(listeners.values)
      }
      listeners = el.listeners

      reconciler = el.reconciler
  }

  def applyPropsToNode(props:Iterable[PropVal]):Unit = {
    for {
      n <- domNode
      p <- props
    } {
      n.asInstanceOf[js.Dynamic].updateDynamic(p.name)(p.value)
    }
  }

  def applyAttrsToNode(as:Iterable[AttrVal]):Unit = {
    for { n <- domNode; a <- as } {
      n.setAttribute(a.name, a.value)
    }
  }

  def removeAttrsFromNode(as:Iterable[AttrVal]):Unit = {
    for { n <- domNode; a <- as } {
      n.removeAttribute(a.name)
    }
  }

  def applyLsntrsToNode(as:Iterable[Lstnr]):Unit = {
    for { n <- domNode; a <- as } {
      n.addEventListener(a.`type`, eventDispatch, false)
    }
  }

  def removeLsntrsFromNode(as:Iterable[Lstnr]):Unit = {
    for { n <- domNode; a <- as } {
      n.removeEventListener(a.`type`, eventDispatch, false)
    }
  }

  def style(s:InlineStyle*) = {
    styles ++= s
    this
  }

  def applyStylesToNode(as:Iterable[InlineStyle]):Unit = {
    domNode match {
      case Some(h:html.Element) => as.foreach({x => h.style.setProperty(x.name, x.value) })
      case _ => // nothing
    }
  }

  def removeStylesFromNode(as:Iterable[InlineStyle]):Unit = {
    domNode match {
      case Some(h:html.Element) => as.foreach({x => h.style.removeProperty(x.name) })
      case _ => // nothing
    }
  }

  def attrs(attrs:AttrVal*) = {
    attributes ++= attrs.map({ x => x.name -> x }).toMap
    this
  }

  def prop(a:PropVal):DElement[T] = {
    properties += a.name -> a
    this
  }

  def addChildren(ac:VNode[dom.Node]*):DElement[T] = {
    children = children ++ ac
    this
  }

  def apply(ac: <.DElAppliable[T] *):DElement[T] = {
    ac.foreach(_.applyTo(this))
    this
  }

  def on(l: Lstnr *) = {
    listeners ++= l.map({x => x.`type` -> x }).toMap
    this
  }


  def create() = {
    val e = dom.document.createElementNS(ns, name).asInstanceOf[T]

    for { AttrVal(a, value) <- attributes.values } {
      e.setAttribute(a, value)
    }

    for { Lstnr(t, _, cap) <- listeners.values } {
      e.addEventListener(t, eventDispatch, cap)
    }

    applyStylesToNode(styles)

    e
  }

  /**
    * A DNode can itself have multiple nodes. We need to be able to get a NodeOps that can perform low-level operations
    * such as insterting and replacing nodes in the tree. This method should return one if the DNode is attached.
    *
    * By default, the implementation of this function assumes we are just adding any children to the top-level
    * domNode. Subclasses that wish to add their children elsewhere should override this.
    *
    * @return
    */
  override def nodeOps: Option[NodeOps[Node]] = domNode.map(DefaultNodeOps(_))

}


object < {

  type VHTMLElement = DElement[html.Element]
  type VSVGElement = DElement[dom.svg.Element]
  type VDOMElement = DElement[dom.Element]


  type HTMLAppliable = DElAppliable[html.Element]
  type SVGAppliable = DElAppliable[dom.svg.Element]

  trait DElAppliable[-T <: dom.Element] {
    def applyTo[TT <: T](d:DElement[TT]):Unit
  }



  implicit class DEAVNode(val vNode: VNode[dom.Node]) extends DElAppliable[dom.Element] {
    override def applyTo[T <: dom.Element](d: DElement[T]): Unit = d.addChildren(vNode)
  }

  implicit class DEAIVNode(val nodes: Iterable[VNode[dom.Node]]) extends DElAppliable[dom.Element] {
    override def applyTo[T <: dom.Element](d: DElement[T]):Unit = d.addChildren(nodes.toSeq:_*)
  }

  implicit class DEAAttr(val a: AttrVal) extends DElAppliable[dom.Element] {
    override def applyTo[T <: dom.Element](d: DElement[T]): Unit = d.attrs(a)
  }

  implicit class DEAProp(val a: PropVal) extends DElAppliable[dom.Element] {
    override def applyTo[T <: dom.Element](d: DElement[T]): Unit = d.prop(a)
  }

  implicit class DEALstnr(val l: Lstnr) extends DElAppliable[dom.Element] {
    override def applyTo[T <: dom.Element](d: DElement[T]): Unit = d.on(l)
  }

  implicit class DEAStyle(val s:InlineStyle) extends DElAppliable[html.Element] {
    override def applyTo[T <: dom.Element](d: DElement[T]): Unit = d.style(s)
  }

  implicit class DEAText(t: String) extends DElAppliable[dom.Element] {
    override def applyTo[T <: dom.Element](d: DElement[T]): Unit = d.addChildren(Text(t))
  }

  implicit class DEAOption[T, E <: dom.Element](opt:Option[T])(implicit ev: T => DElAppliable[E]) extends DElAppliable[E] {
    override def applyTo[TT <: E](d: DElement[TT]): Unit = opt.foreach(ev(_).applyTo(d))
  }

  class DEAAction[T <: dom.Element](f: DElement[T] => Unit) extends DElAppliable[T] {
    def applyTo[TT <: T](d:DElement[TT]) = f(d)
  }

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

  def svg = SVG.svg

  def apply(n:String):VHTMLElement = DElement[html.Element](n, ns=DElement.htmlNS)

  def applyT[T <: dom.Element](n:String):DElement[T] = DElement[T](n, ns=DElement.htmlNS)

  def apply[T <: dom.Element](n:String, u:String = "", ns:String):DElement[T] = DElement[T](n, if (u.isEmpty) None else Some(u), ns)

}

object SVG {

  def apply(ac: <.DElAppliable[dom.svg.Element] *):DElement[dom.svg.Element] = <.apply("svg", ns=DElement.svgNS)(ac:_*)

  def svg = <.apply[org.scalajs.dom.svg.SVG]("svg", ns=DElement.svgNS)

  def circle = <.apply[dom.svg.Circle]("circle", ns=DElement.svgNS)

  def ellipse = <.apply[dom.svg.Ellipse]("ellipse", ns=DElement.svgNS)

  def polygon = <.apply[dom.svg.Polygon]("polygon", ns=DElement.svgNS)

  def line = <.apply[dom.svg.Line]("line", ns=DElement.svgNS)

  def text = <.apply[dom.svg.Text]("text", ns=DElement.svgNS)

  def tspan = <.apply[dom.svg.TSpan]("tspan", ns=DElement.svgNS)

  def g = <.apply[dom.svg.G]("g", ns=DElement.svgNS)

  def path = <.apply[dom.svg.Path]("path", ns=DElement.svgNS)

  def rect = <.apply[dom.svg.Element]("rect", ns=DElement.svgNS)

  def foreignObject = <.apply[dom.svg.Element]("foreignObject", ns=DElement.svgNS)

}


object ^ {

  case class Attrable(n:String) {
    def :=(s:String) = AttrVal(n, s)

    def :=(i:Int) = AttrVal(n, i.toString)

    def :=(d:Double) = AttrVal(n, d.toString)

    def ?=(o:Option[String]) = o.map(:=)
  }

  case class Propable(n:String) {
    def :=(j:String) = PropVal(n, j)

    def ?=(j:Option[String]) = PropVal(n, j.orNull[String])
  }

  object Keyable {
    def :=(k: String): <.DElAppliable[Element] = new <.DElAppliable[dom.Element] {
      override def applyTo[TT <: Element](d: DElement[TT]): Unit = {
        d.uniqEl = Some(k)
      }
    }
  }

  object reconciler {
    def :=(r:Reconciler) = new DEAAction[dom.Element]({ x => x.reconciler = r })
  }

  def key = Keyable

  def attr(x:String) = Attrable(x)

  def prop(n:String) = Propable(n)

  def alt = Attrable("alt")
  def src = Attrable("src")
  def `class` = Attrable("class")
  def cls = `class`
  def role = Attrable("role")
  def href = Attrable("href")

  case class Lsntrable(n:String) {
    def -->(e: => Unit ) = Lstnr(n, (x:Event) => e, false)

    def ==>(f: (Event) => Unit) = {
      Lstnr(n, f, false)
    }
  }

  def onClick = Lsntrable("click")
  def on(s:String) = Lsntrable(s)

  case class InlineStylable(n:String) {
    def :=(v:String) = InlineStyle(n, v)
  }

  def minHeight = InlineStylable("min-height")
  def backgroundImage = InlineStylable("background-image")

}
