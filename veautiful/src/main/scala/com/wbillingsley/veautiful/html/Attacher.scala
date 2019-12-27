package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.VNode
import org.scalajs.dom

object Attacher {

  class RootNode(el:dom.Element) {
    val root = DElement("root")
    root.domNode = Some(el)

    def render(e:VNode[dom.Node]) = {
      root.updateChildren(Seq(e))
    }
  }

  def newRoot(el:dom.Element) = {
    el.innerHTML = ""
    new RootNode(el)
  }

}

