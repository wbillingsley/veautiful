package com.wbillingsley.veautiful

/**
  * A node in the Virtual DOM.
  *
  * Once attached, it controls its node. It is up to the VNode to "play nicely".
  *
  * @tparam N the type of node this VNode controls. E.g., dom.Node, dom.Element, but could be any target
  */
trait VNode[+N] extends HasRetention with HasTestMatch {

  /**
    * The dom node that this is currently attached to.
    *
    * Note that if a VNode uses more than one real node to implement itself, parent.get.domNode.get might not be
    * the same as domNode.get.getParent(), even if the gets were to succeed.
    */
  def domNode:Option[N]

  /**
    * The parent node in the Virtual DOM. As we only do one-way connections from the Virtual DOM to the
    * real DOM, you'll need this to navigate the tree.
    *
    * Note that if a VNode uses more than one real node to implement itself, parent.get.domNode.get might not be
    * the same as domNode.get.getParent(), even if the gets were to succeed.
    */
  //var parent:Option[VNode[_]] = None

  /**
    * Called before a detach operation
    */
  def beforeDetach():Unit = {}

  /**
    * Called before an attach operation
    */
  def beforeAttach():Unit = {}

  /**
    * Called after a detach operation
    */
  def afterDetach():Unit = {}

  /**
    * Called after an attach operation
    */
  def afterAttach():Unit = {}

  /**
    * Called to perform an attach operation -- ie, create the real DOM node and put it into
    * domNode
    */
  def attach():N

  /**
    * Called to perform a detach operation -- ie, anything necessary to clean up the DOM node,
    * and then remove it from domNode so we know it's gone.
    */
  def detach():Unit

  /**
    * To make explicit that the semantics of whether there is a DOM node attached are whether this
    * VNode has put one in domNode.
    */
  def isAttached:Boolean = domNode.nonEmpty

}