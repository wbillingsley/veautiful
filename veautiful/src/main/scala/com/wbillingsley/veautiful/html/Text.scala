package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.VNode
import org.scalajs.dom

case class Text(text:String) extends VNode[dom.Node] {

  var domNode:Option[dom.Node] = None

  def create() = {
    dom.document.createTextNode(text)
  }

  def attach() = {
    val n = create()
    domNode = Some(n)
    n
  }

  def detach() = {
    domNode = None
  }

}