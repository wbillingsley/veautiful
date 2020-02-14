package com.wbillingsley.veautiful

import org.scalajs.dom
import org.scalajs.dom.Node

/**
  * As DNodes can themselves be made up of multiple nodes, we need some node operations for adding children into
  * the DOM.
  */
trait NodeOps[N] {

  def appendAttachedChild(v:VNode[N]):Unit

  def removeAttachedChild(v:VNode[N]):Unit

  def insertAttachedChildBefore(v:VNode[N], before:VNode[N]):Unit

  def replaceAttachedChild(newNode:VNode[N], oldNode:VNode[N]):Unit

  /**
    * Insert operations can move DOM nodes. This means that by the time we get to process a child VNode for removal, it
    * might already have been moved elsewhere in the tree. Consequently, we want to verify that the DOM node still
    * has this node as a parent.
    * @param node
    * @return
    */
  def nodeIsChildOfMine(node:VNode[N]):Boolean

}

/**
  * A default set of node operations that acts on a given node.
  */
case class DefaultNodeOps(n:dom.Node) extends NodeOps[dom.Node] {
  override def appendAttachedChild(c:VNode[dom.Node]): Unit = c.domNode.foreach(n.appendChild)

  override def removeAttachedChild(c: VNode[dom.Node]): Unit = c.domNode.foreach(n.removeChild)

  override def insertAttachedChildBefore(c: VNode[dom.Node], before: VNode[dom.Node]): Unit = for {
    cc <- c.domNode; bb <- before.domNode
  } n.insertBefore(cc, bb)

  override def replaceAttachedChild(newNode: VNode[dom.Node], oldNode: VNode[dom.Node]): Unit = for {
    nc <- newNode.domNode; oc <- oldNode.domNode
  } n.replaceChild(nc, oc)

  override def nodeIsChildOfMine(node: VNode[dom.Node]): Boolean = {
    node.domNode.map(_.parentNode) contains n
  }
}


/**
  * A DNode has a create and a makeItSo
  */
trait DNode[+N, C] extends VNode[N] {

  /**
    * A DNode can itself have multiple nodes. We need to be able to get a NodeOps that can perform low-level operations
    * such as insterting and replacing nodes in the tree. This method should return one if the DNode is attached.
    *
    * By default, the implementation of this function assumes we are just adding any children to the top-level
    * domNode. Subclasses that wish to add their children elsewhere should override this.
    * @return
    */
  def nodeOps:Option[NodeOps[C]]// = domNode.map(DefaultNodeOps)

  /**
    * Children VNodes
    */
  def children:collection.Seq[VNode[C]]

  override def beforeAttach(): Unit = {
    super.beforeAttach()
    for { d <- children } d.beforeAttach()
  }

  /**
    * Creates this node (not its children) and attaches this DNode to it. This is called by the default implementation
    * of attach - which attaches this node and then recurses down the children.
    * @return
    */
  def attachSelf():N

  def attach():N = {
    val n = attachSelf()

    for { ch <- children } {
      ch.attach()
    }

    for {
      ops <- nodeOps
      d <- children
    } {
      ops.appendAttachedChild(d)
    }

    n
  }

  /**
    * After this element has been attached, recurse down through the children calling afterAttach
    */
  override def afterAttach(): Unit = {
    super.afterAttach()
    for {
      d <- children
    } d.afterAttach()
  }

  override def beforeDetach(): Unit = {
    super.beforeDetach()
    for { d <- children } d.beforeDetach()
  }

  /**
    * Detaches this node (assuming all its children are already detached). This is called by the default implementation
    * of detach, which detaches its children and then calls `detachSelf()`
    */
  def detachSelf():Unit

  def detach() = {
    // First, we remove the children from being children in the view tree
    for {
      ops <- nodeOps
      d <- children if ops.nodeIsChildOfMine(d)
    } {
      ops.removeAttachedChild(d)
    }

    // Then we ask the children to detach themselves
    for {
      d <- children
    } {
      d.detach()
    }

    // Then we detach ourself
    detachSelf()
  }

  override def afterDetach(): Unit = {
    super.afterDetach()
    for { d <- children } d.afterDetach()
  }

}