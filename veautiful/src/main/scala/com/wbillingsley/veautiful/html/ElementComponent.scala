package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.VNode
import org.scalajs.dom
import com.wbillingsley.veautiful.Blueprint

/*
 * A component that renders itself into the children of a fixed element
 */
class ElementComponent[T <: dom.Element](val el:DElement[T]) extends VNode[dom.Node] {

  def domNode = el.domNode

  def attach() = {
    if (isAttached) {
      throw new IllegalStateException("Attached twice")
    }
    el.attach()
  }

  def detach() = {
    el.detach()
  }

  def renderElements(ch:VNode[dom.Node] | Blueprint[VNode[dom.Node]]) = el.updateChildren(Seq(ch))

}






