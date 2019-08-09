package com.wbillingsley.veautiful

import org.scalajs.dom

object Attacher {

  class RootNode(el:dom.Element) {
    val root = DElement("root")
    root.domNode = Some(el)

    def render(e:VNode) = {
      root.updateChildren(Seq(e))
    }
  }

  def newRoot(el:dom.Element) = {
    el.innerHTML = ""
    new RootNode(el)
  }

}

