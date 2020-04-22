package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.reconcilers.Reconciler
import com.wbillingsley.veautiful.{DefaultNodeOps, DiffNode, NodeOps, VNode}
import org.scalajs.dom
import org.scalajs.dom.{Element, Node}

object Attacher {

  class RootNode(el:dom.Element) extends VHtmlDiffNode {

    override def reconciler: Reconciler = Reconciler.default

    var domNode:Option[dom.Element] = None

    def render(e: VNode[dom.Node]):Unit = {
      updateChildren(Seq(e))
    }

    override def updateSelf: PartialFunction[DiffNode[_, Node], _] = { case _ => /* Do Nothing */ }

    override def nodeOps: Option[NodeOps[Node]] = domNode.map(DefaultNodeOps(_))

    override def attachSelf(): Element = {
      domNode = Some(el)
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

