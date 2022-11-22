package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.reconcilers.Reconciler
import com.wbillingsley.veautiful.{DefaultNodeOps, DiffNode, NodeOps, VNode}
import org.scalajs.dom
import org.scalajs.dom.{Element, Node}

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

    def render(e: VNode[dom.Node]):Unit = {
      _children = reconciler.updateChildren(this, collection.Seq(e))
    }

    override def makeItSo = 
      case e:VNode[dom.Node] @unchecked => render(e)

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

  def newRoot(el:dom.Element) = {
    val rn = new RootNode(el)
    rn.attach()
    rn
  }

}

