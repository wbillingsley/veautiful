package com.wbillingsley.veautiful

import org.scalajs.dom


object Attacher {

  class RootNode(el:dom.Element, vNode:VNode) {
    val root = DElement("root")
    root.domNode = Some(el)

    def render(e:VNode) = {
      root.updateChildren(Seq(e))
    }
  }

  def render(vdom:VNode, el:dom.Element) = {
    el.innerHTML = ""
    new RootNode(el, vdom)
  }

}

/*
 * A component that renders itself into the children of a fixed element
 */
class ElementComponent(el:DElement) extends VNode {

  def domNode = el.domNode

  def attach() = {
    if (isAttached) {
      throw new IllegalStateException("Attached twice")
    }
    el.attach()
  }

  def detach() = el.detach()

  def renderElements(ch:VNode) = el.updateChildren(Seq(ch))

}






