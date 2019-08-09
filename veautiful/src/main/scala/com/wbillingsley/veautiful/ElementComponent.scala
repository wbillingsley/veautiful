package com.wbillingsley.veautiful

import org.scalajs.dom

/*
 * A component that renders itself into the children of a fixed element
 */
class ElementComponent(val el:DElement) extends VNode {

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






