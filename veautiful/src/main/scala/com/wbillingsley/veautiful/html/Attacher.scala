package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.reconcilers.Reconciler
import com.wbillingsley.veautiful.{DefaultNodeOps, DiffNode, NodeOps, VNode, Blueprint, Update}
import org.scalajs.dom
import org.scalajs.dom.{Element, Node}
import java.{util => ju}
import scalajs.js

/**
 * Responsible for mounting Veautiful scenes into an HTML document
 */ 
object Attacher {

  /**
    * Renders the scene. 
    *
    * @param el the DOM element into which to render the scene
    */
  class RootNode(el:dom.Element) extends DiffNode[dom.Node, dom.Node] with Update {

    protected var _children:collection.Seq[VNode[dom.Node]] = collection.Seq.empty

    protected var _lastRendered:collection.Seq[VDomContent] = Seq.empty

    override def children = _children

    override def reconciler: Reconciler = Reconciler.default

    var domNode:Option[dom.Element] = None

    def render(e: VDomContent):Unit = {
      _lastRendered = collection.Seq(e)
      _children = reconciler.updateChildren(this, _lastRendered)
    }

    override def makeItSo = 
      case e:VNode[dom.Node] @unchecked => render(e)
      case e:Blueprint[VNode[dom.Node]] @unchecked => render(e)

    override def nodeOps: Option[NodeOps[Node]] = domNode.map(DefaultNodeOps(_))

    override def attachSelf(): Element = {
      domNode = Some(el)
      el.innerHTML = ""
      el
    }

    override def detachSelf(): Unit = {
      domNode = None
    }

    /** 
     * Re-renders the content based on the existing blueprints or nodes 
     */
    def rerender() = 
      _children = reconciler.updateChildren(this, _lastRendered)

    def update() = rerender()

    /**
      * Dynamically imports a JS module, rerendering this root when it is loaded
      */
    def importAndRefresh[T <: js.Any](url:String):js.Promise[T] = 
      val promise = js.`import`[T](url)
      promise.`then`(_ => update())
      promise
  }

  /** Creates a new root element */
  def newRoot(el:dom.Element) = {
    val rn = new RootNode(el)
    rn.attach()
    rn
  }

  def newRootById(id:String):RootNode = 
    val el = dom.document.getElementById(id)
    if el == null then 
      dom.console.error(s"No element found with id $id") 
      throw ju.NoSuchElementException(s"No element found with selector $id")
    else newRoot(el)

  def newRoot(selector:String):RootNode = 
    val el = dom.document.querySelector(selector)
    if el == null then 
      dom.console.error(s"No element found with selector $selector") 
      throw ju.NoSuchElementException(s"No element found with selector $selector")
    else newRoot(el)

  def mount(selector:String, e: VNode[dom.Node] | Blueprint[VNode[dom.Node]]) = 
      val r = newRoot(selector)
      r.render(e)
      r

  def mountToBody(e: VNode[dom.Node] | Blueprint[VNode[dom.Node]]) = 
    val r = newRoot(dom.document.body)
    r.render(e)
    r

  def mount(el:dom.Element, e: VNode[dom.Node] | Blueprint[VNode[dom.Node]]) = 
    val r = newRoot(el)
    r.render(e)
    r
  
  /**
    * Installs an element into the document head. 
    * 
    * This is treated differently than ordinary VNodes, in that most things installed into the head (e.g. scripts) are
    * not intended to be uninstalled later. 
    *
    * @param e
    */
  def installInHead(v: VNode[dom.Node]):Unit = 
    v.beforeAttach()
    org.scalajs.dom.document.head.append(v.attach())
    v.afterAttach()

  def installInHead(b: Blueprint[VNode[dom.Node]]):Unit = 
    installInHead(b.build())

}

export Attacher.*

