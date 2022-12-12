package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.reconcilers.Reconciler
import com.wbillingsley.veautiful.{DefaultNodeOps, DiffNode, NodeOps, VNode, Blueprint}
import org.scalajs.dom
import org.scalajs.dom.{Element, Node}
import java.{util => ju}

/**
 * Responsible for mounting Veautiful scenes into an HTML document
 */ 
object Attacher {

  /**
    * Renders the scene. 
    *
    * @param el the DOM element into which to render the scene
    */
  class RootNode(el:dom.Element) extends DiffNode[dom.Node, dom.Node] {

    protected var _children:collection.Seq[VNode[dom.Node]] = collection.Seq.empty
    override def children = _children

    override def reconciler: Reconciler = Reconciler.default

    var domNode:Option[dom.Element] = None

    def render(e: VNode[dom.Node] | Blueprint[VNode[dom.Node]]):Unit = {
      _children = reconciler.updateChildren(this, collection.Seq(e))
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


  def mount(el:dom.Element, e: VNode[dom.Node] | Blueprint[VNode[dom.Node]]) = 
    val r = newRoot(el)
    r.render(e)
    r
  
}

export Attacher.*

