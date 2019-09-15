package com.wbillingsley.veautiful

import org.scalajs.dom
import org.scalajs.dom.Node

/**
  * As DNodes can themselves be made up of multiple nodes, we need some node operations for adding children into
  * the DOM.
  */
trait NodeOps {

  def appendAttachedChild(v:VNode):Unit

  def removeAttachedChild(v:VNode):Unit

  def insertAttachedChildBefore(v:VNode, before:VNode):Unit

  def replaceAttachedChild(newNode:VNode, oldNode:VNode):Unit

}

/**
  * A default set of node operations that acts on a given node.
  */
case class DefaultNodeOps(n:dom.Node) extends NodeOps {
  override def appendAttachedChild(c:VNode): Unit = c.domNode.foreach(n.appendChild)

  override def removeAttachedChild(c: VNode): Unit = c.domNode.foreach(n.removeChild)

  override def insertAttachedChildBefore(c: VNode, before: VNode): Unit = for {
    cc <- c.domNode; bb <- before.domNode
  } n.insertBefore(cc, bb)

  override def replaceAttachedChild(newNode: VNode, oldNode: VNode): Unit = for {
    nc <- newNode.domNode; oc <- oldNode.domNode
  } n.replaceChild(nc, oc)
}


/**
  * A DNode has a create and a makeItSo
  */
trait DNode extends VNode {

  def create():dom.Element

  var domNode:Option[dom.Element]

  /**
    * A DNode can itself have multiple nodes. We need to be able to get a NodeOps that can perform low-level operations
    * such as insterting and replacing nodes in the tree. This method should return one if the DNode is attached.
    *
    * By default, the implementation of this function assumes we are just adding any children to the top-level
    * domNode. Subclasses that wish to add their children elsewhere should override this.
    * @return
    */
  def nodeOps:Option[NodeOps] = domNode.map(DefaultNodeOps)

  /**
    * Children VNodes
    */
  def children:Seq[VNode]

  def attach():dom.Element = {
    val n = create()
    domNode = Some(n)

    for { ch <- children } {
      ch.beforeAttach()
      ch.attach()
    }

    for {
      ops <- nodeOps
      d <- children
    } {
      ops.appendAttachedChild(d)
      d.afterAttach()
    }

    n
  }

  def detach() = {
    for {
      ops <- nodeOps
      d <- children
    } {
      d.beforeDetach()
      ops.removeAttachedChild(d)
    }

    for {
      d <- children
    } {
      d.detach()
      d.afterDetach()
    }

    domNode = None
  }

}