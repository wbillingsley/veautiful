package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful
import com.wbillingsley.veautiful.reconcilers.Reconciler
import com.wbillingsley.veautiful.{DefaultNodeOps, DiffNode, NodeOps, VNode, Blueprint, StateVariable, DynamicValue}
import org.scalajs.dom
import org.scalajs.dom.{Element, Event, Node, html}

import scala.collection.mutable
import scala.scalajs.js
import com.wbillingsley.veautiful.Retention

sealed trait PredefinedElementChild
object PredefinedElementChild {
  case class AttrVal(name:String, value:String) extends PredefinedElementChild
  case class PropVal(name:String, value:js.Any) extends PredefinedElementChild
  case class KeyVal(key:Any) extends PredefinedElementChild
  case class InlineStyle(name:String, value:String) extends PredefinedElementChild
  case class EventListener[-T <: Event](`type`:String, func:Function[T, _], capture:Boolean=false) extends PredefinedElementChild //TODO: Add other flags, e.g. once & passive
}

object DElement {
  @deprecated("moved to top-level definition html.NS or html.htmlNS in 0.3-M2")
  val htmlNS = "http://www.w3.org/1999/xhtml"
  
  @deprecated("moved to top-level definition svg.NS or html.svgNS in 0.3-M2")
  val svgNS = "http://www.w3.org/2000/svg"
}

/**
  * An individual item that can be passed into the `apply` method of a DElement. e.g. in
  * 
  * <.button(^.onClick --> println("bang"), "Hello ", <.b("World"))
  */
type SingleElementChild[-T <: dom.Element] = String | VNode[dom.Node] | Blueprint[VNode[dom.Node]] | PredefinedElementChild | CustomElementChild[T]

/**
  * Things that can be arguments to the DElement's apply method. 
  * This allows Sequences, Options, etc of SingleChild to be included, simplifying the syntax
  */
type ElementChild[-T <: dom.Element] = SingleElementChild[T] | Iterable[SingleElementChild[T]]

/**
  * Allows libraries to define their own operations that can be passed into the apply method of an element in a DSL
  */
trait CustomElementChild[-T <: dom.Element] {
  def applyTo[TT <: T](d:DElement[TT]):Unit
}

class ElementAction[T <: dom.Element](f: DElement[T] => Unit) extends CustomElementChild[T] {
  def applyTo[TT <: T](d:DElement[TT]) = f(d)
}


/**
 * Represents a DOM Element using a Virtual DOM-like strategy (reconciling its children). The D is for DOM or Diff.
 * 
 * As many other components will render a tree such as:
 * 
 * {{{
 * div(
 *   ul(li("item1"), li("item2"), li("item3"))
 * )
 * }}}
 * 
 * DElement ends up being the heart of how the kit works.
 * 
 * Its apply method accepts a number of ElementChidren / modifiers. This can cover any number of different kinds of setting - e.g. 
 * attributes, properties, listeners, but also things like changing the reconciliation strategy or adding a key to the element to 
 * make it more likely to be retained in a reconciliation.
 */
class DElement[+T <: dom.Element](name:String, var uniqEl:Option[Any] = None, ns:String=NS) extends DiffNode[T, dom.Node] {

  import PredefinedElementChild.*

  /**
   * Our DOM elements can be given a key in their properties, so the retention strategy is dynamic: Keyed if a key is set; Keep(element name) if not
   */
  override def retention: Retention = uniqEl match {
    case Some(key) => Retention.Keyed(key)
    case _ => Retention.Keep((name, ns))
  }

  private var attributes:mutable.Map[String, PredefinedElementChild.AttrVal] = mutable.Map.empty

  private var properties:mutable.Map[String, PropVal] = mutable.Map.empty

  private var _children:collection.Seq[VNode[dom.Node]] = Seq.empty

  override def children = _children

  var listeners:Map[String, EventListener[_ <: Event]] = Map.empty

  var styles:Seq[InlineStyle] = Seq.empty

  var reconciler:Reconciler = Reconciler.default

  private[this] var _domNode:Option[T] = None

  def domNode:Option[T] = _domNode

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
    } h.func.asInstanceOf[Function[Event, _]].apply(e)
  }

  private def updateAttributes(incoming:mutable.Map[String, AttrVal]):Unit = {
    // Update attributes
    for { n <- domNode } {
      for {
        (k, _) <- attributes if !incoming.contains(k)
      } n.removeAttribute(k)

      for {
        (k, v) <- incoming if !attributes.get(k).contains(v)
      } n.setAttribute(v.name, v.value)
    }

    attributes = incoming
  }

  private def updateProperties(incoming:mutable.Map[String, PropVal]):Unit = {
    // Update properties
    properties = incoming
    for v <- properties.values do applyPropToNode(v)
  }

  private def updateStyles(incoming:Seq[InlineStyle]):Unit = {
    removeStylesFromNode(styles)
    applyStylesToNode(incoming)
    styles = incoming
  }

  private def updateListeners(incoming:Map[String, EventListener[? <: Event]]):Unit = {
    if (listeners.keys != incoming.keys) {
      removeLsntrsFromNode(listeners.values)
      applyLsntrsToNode(incoming.values)
    }
    listeners = incoming
  }

  /** Called internally by MakeItSo to morph this DElement to match a target */
  private def updateSelf(el:DElement[T]) = {
    updateAttributes(el.attributes)
    updateProperties(el.properties)
    updateStyles(el.styles)
    updateListeners(el.listeners)

    reconciler = el.reconciler
  }

  private def applyPropToNode(p:PropVal):Unit = {
    for n <- domNode do
      n.asInstanceOf[js.Dynamic].updateDynamic(p.name)(p.value)
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

  def applyLsntrsToNode(as:Iterable[EventListener[_]]):Unit = {
    for { n <- domNode; a <- as } {
      n.addEventListener(a.`type`, eventDispatch, false)
    }
  }

  def removeLsntrsFromNode(as:Iterable[EventListener[_]]):Unit = {
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
    //attributes.addAll(attrs.map({ x => x.name -> x }))
    for { a <- attrs } attributes(a.name) = a
    this
  }

  def prop(a:PropVal):DElement[T] = {
    properties += a.name -> a
    this
  }

  def addChildren(ac:VNode[dom.Node]*):DElement[T] = {
    _children = _children ++ ac
    this
  }

  
  def apply(ac: ElementChild[T] *):DElement[T] = {
    // The @unchecked annotations in here should be ok, so long as ElementChild's definition
    // doesn't have duplicate outer types. E.g. we have VNode[dom.Node] but no other
    // types of VNode in the type union.
    for child <- ac do child match
      case n:VNode[dom.Node] @unchecked => addChildren(n)
      case b:Blueprint[VNode[dom.Node]] @unchecked => addChildren(b.build())
      case s:String => addChildren(Text(s))
      case a:AttrVal => attrs(a)
      case p:PropVal => prop(p)
      case KeyVal(k) => uniqEl = Option(k)
      case s:InlineStyle => style(s)
      case l:EventListener[_] => on(l)
      case appliable:CustomElementChild[T] @unchecked => appliable.applyTo(this)
      case i:Iterable[SingleElementChild[T]] @unchecked => i.foreach((sc) => apply(sc))
    this
  }

  def on(l: EventListener[_] *) = {
    listeners ++= l.map({x => x.`type` -> x }).toMap
  }


  def create():T = {
    val e = dom.document.createElementNS(ns, name).asInstanceOf[T]

    for { AttrVal(a, value) <- attributes.values } {
      e.setAttribute(a, value)
    }

    for { EventListener(t, _, capture) <- listeners.values } {
      e.addEventListener(t, eventDispatch, capture)
    }

    applyStylesToNode(styles)

    e
  }

  /** Sets an attribute on this DElement and (if attached) its node */
  def setAttribute(value:AttrVal):Unit = {
    if !attributes.get(value.name).contains(value) then
      attributes(value.name) = value
      for n <- domNode do n.setAttribute(value.name, value.value)
  }

  /** Sets a property on this DElement and (if attached) its node. */
  def setProperty(value:PropVal):Unit = {
    if !properties.get(value.name).contains(value) then
      properties(value.name) = value
      applyPropToNode(value)
  }

  def updateChildren(to:collection.Seq[VNode[dom.Node] | Blueprint[VNode[dom.Node]]]):Unit = 
     _children = reconciler.updateChildren(this, to)


  /**
    * A ParentNode can itself have multiple nodes. We need to be able to get a NodeOps that can perform low-level operations
    * such as insterting and replacing nodes in the tree. This method should return one if the ParentNode is attached.
    *
    * By default, the implementation of this function assumes we are just adding any children to the top-level
    * domNode. Subclasses that wish to add their children elsewhere should override this.
    *
    * @return
    */
  override def nodeOps: Option[NodeOps[Node]] = domNode.map(DefaultNodeOps(_))

  override def makeItSo = {
    // We leave this unchecked because conventionally, we'll only be calling makeItSo if the target has matched the retention strategy
    case to:DElement[T] @unchecked => 
      updateSelf(to)
      updateChildren(to.children)
    case bp:DElementBlueprint[T] @unchecked => 
      val target = bp.build() // TODO: Update directly from a DElementBlueprint for better efficiency
      makeItSo(target)
  }
}

/**
  * A Blueprint for a DElement
  *
  * @param name the tag of the element
  * @param key will cause the element to use the Keyed retention strategy
  * @param ns the namespace of the element
  * @param modifiers any number of ElementChildren, applied in order
  */
class DElementBlueprint[+T <: dom.Element](name:String, ns:String = DElement.htmlNS, modifiers:Seq[ElementChild[T]] = scala.collection.immutable.ArraySeq.empty) 
  extends Blueprint[DElement[T]](classOf[DElement[T]]) {

    def apply(modifiers:ElementChild[T]*):DElementBlueprint[T] = DElementBlueprint[T](name, ns, this.modifiers ++ modifiers)

    /** Our key is set by the modifiers. As it's possible to apply ^.key := more than once, we pick the last one. */
    lazy val lastKey:Option[Any] = modifiers.collect({ case PredefinedElementChild.KeyVal(x) => x}).lastOption

    override def build(): DElement[T] = DElement(name, key, ns)(modifiers*)

    /** Alias for build, to be explicit it's the VNode not the DOM node we're building */
    def vnode() = build()

    /**
     * Our DOM elements can be given a key in their properties, so the retention strategy is dynamic: Keyed if a key is set; Keep(element name) if not
     */
    override def retention: Retention = lastKey match {
      case Some(key) => Retention.Keyed(key)
      case _ => Retention.Keep((name, ns))
    }    

}

/** A builder for mutable DElements, so that we can declare <.mutable.div() etc in the DSLs */
class DElementBuilder[Base <: dom.Element](defaultTag:String, ns:String) extends DSLFactory[DElement, Base] {
  
  def apply(n:String):DElement[Base] = DElement[Base](n, ns=ns)

  def apply(modifiers:ElementChild[Base]*):DElement[Base] = DElement[Base](defaultTag, ns=ns)(modifiers*)

  def applyT[T <: dom.Element](n:String):DElement[T] = DElement[T](n, ns=ns)

  def apply[T <: dom.Element](n:String, u:String = "", ns:String):DElement[T] = 
    if u.isEmpty then DElement[T](n, Some(u), ns) else DElement[T](n, None, ns)

}

/**
  * Trait implemented by the HTML and SVG objects
  *
  * @param defaultTag
  * @param defaultNS
  */
class DBlueprintBuilder[T <: dom.Element](defaultTag:String, defaultNS:String) extends DSLFactory[DElementBlueprint, T] {

  def apply(n:String):DElementBlueprint[T] = DElementBlueprint[T](n, ns=defaultNS)

  def apply(modifiers:ElementChild[T]*):DElementBlueprint[T] = DElementBlueprint(defaultNS)(modifiers*)

  def applyT[T <: dom.Element](n:String):DElementBlueprint[T] = DElementBlueprint[T](n, ns=defaultNS)

  def apply[T <: dom.Element](n:String, u:String = "", ns:String):DElementBlueprint[T] = 
    if u.isEmpty then DElementBlueprint[T](n, ns) else DElementBlueprint[T](n, ns)(^.key := u)
  
}



trait ModifierDSL {

  import PredefinedElementChild.*

  class Attrable(n:String) {
    def :=(s:String) = AttrVal(n, s)

    def :=(i:Int) = AttrVal(n, i.toString)

    def :=(d:Double) = AttrVal(n, d.toString)

    def ?=(o:Option[String]) = o.map(:=)

    def <--[T] (dv:DynamicValue[T]) = DynamicModifier.DynamicAttr(n, dv)
  }

  case class Propable[JSType <: js.Any](n:String) {
    def :=(j:JSType) = PropVal(n, j)

    def ?=(j:Option[JSType]) = PropVal(n, j.orNull[JSType | Null])

    def <--[T <: JSType] (dv:DynamicValue[T]) = DynamicModifier.DynamicProp(n, dv)
  }

  import scala.language.dynamics

  /**
   * Supports `^.prop("foo") := ` and `^.prop.foo :=`
   */
  object prop extends Dynamic {
    def apply(n:String) = Propable[js.Any](n)
    def selectDynamic(s:String) = apply(s)
  }


  object reconciler {
    def :=(r:Reconciler) = new ElementAction[dom.Element]({ x => x.reconciler = r })
  }

  /**
   * Supports ^.key := "my-key", which tends also to change the retention strategy of an element to Keyed
   */
  object key {
    def :=(k: String) = KeyVal(k)
  }

  /**
   * Supports `^.attr("foo") := ` and `^.attr.foo :=`
   */
  object attr extends Dynamic {
    def apply(x:String) = Attrable(x)
    final def selectDynamic(s:String):Attrable = apply(s)
  }


  def alt = attr("alt")
  def style = attr("style")
  def src = attr("src")
  def `class` = attr("class")
  def cls = `class`
  def role = attr("role")
  def href = attr("href")

  case class Lsntrable[T <: Event](n:String) {
    def -->(e: => Unit ) = EventListener[T](n, (x:T) => e, false)

    def ==>(f: (T) => Unit) = {
      EventListener(n, f, false)
    }

    def pushValue(sv:StateVariable[String]) = {
      EventListener(n, (e) => for v <- e.inputValue do sv.value = v, false)
    }
  }

  object on extends Dynamic {
    def apply[T <: Event](s:String) = Lsntrable[T](s)
    def selectDynamic(s:String) = apply[Event](s)
  }

  def onClick = Lsntrable[dom.MouseEvent]("click")
  def onDblClick = Lsntrable[dom.MouseEvent]("dblclick")
  def onMouseDown = Lsntrable[dom.MouseEvent]("mousedown")
  def onMouseEnter = Lsntrable[dom.MouseEvent]("mouseenter")
  def onMouseLeave = Lsntrable[dom.MouseEvent]("mouseleave")
  def onMouseMove = Lsntrable[dom.MouseEvent]("mousemove")
  def onMouseOut = Lsntrable[dom.MouseEvent]("mouseout")
  def onMouseOver = Lsntrable[dom.MouseEvent]("mouseover")
  def onMouseUp = Lsntrable[dom.MouseEvent]("mouseup")

  def onPointerCancel = Lsntrable[dom.PointerEvent]("pointercancel")
  def onPointerDown = Lsntrable[dom.PointerEvent]("pointerdown")
  def onPointerEnter = Lsntrable[dom.PointerEvent]("pointerenter")
  def onPointerLeave = Lsntrable[dom.PointerEvent]("pointerleave")
  def onPointerMove = Lsntrable[dom.PointerEvent]("pointermove")
  def onPointerOut = Lsntrable[dom.PointerEvent]("pointerout")
  def onPointerOver = Lsntrable[dom.PointerEvent]("pointerover")
  def onPointerUp = Lsntrable[dom.PointerEvent]("pointerup")

  def onTouchCancel = Lsntrable[dom.TouchEvent]("touchcancel")
  def onTouchEnd = Lsntrable[dom.TouchEvent]("touchend")
  def onTouchMove = Lsntrable[dom.TouchEvent]("touchmove")
  def onTouchStart = Lsntrable[dom.TouchEvent]("touchstart")

  def onScroll = Lsntrable[dom.Event]("scroll")

  def onBlur = Lsntrable[dom.FocusEvent]("blur")
  def onFocus = Lsntrable[dom.FocusEvent]("focus")
  def onFocusIn = Lsntrable[dom.FocusEvent]("focusin")
  def onFocusOut = Lsntrable[dom.FocusEvent]("focusout")

  def onKeyDown = Lsntrable[dom.KeyboardEvent]("keydown")
  def onKeyUp = Lsntrable[dom.KeyboardEvent]("keyup")

  case class InlineStylable(n:String) {
    def :=(v:String) = InlineStyle(n, v)
  }

  def minHeight = InlineStylable("min-height")
  def backgroundImage = InlineStylable("background-image")

}

def getTargetValue = PushBuilder[dom.Event, String](_.inputValue)
