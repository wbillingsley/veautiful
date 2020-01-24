package com.wbillingsley.veautiful.html

import com.wbillingsley.veautiful.Update
import org.scalajs.dom.{Element, Node}

/**
  * A node for markup languages such as Markdown.
  * @param transform The function that will transform the markup into HTML
  * @param data The markup to transform
  */
private class MarkupNode(transform:(String) => String)(data: => String) extends VHtmlNode with Update {

  private var _domNode: Option[Element] = None
  private var lastData:Option[String] = None

  /**
    * The dom node that this is currently attached to.
    *
    * Note that if a VNode uses more than one real node to implement itself, parent.get.domNode.get might not be
    * the same as domNode.get.getParent(), even if the gets were to succeed.
    */
  override def domNode: Option[Node] = _domNode

  /**
    * Called to perform an attach operation -- ie, create the real DOM node and put it into
    * domNode
    */
  override def attach(): Node = {
    val e = <.div(^.cls := "markup-node").create()
    _domNode = Some(e)
    e
  }

  override def afterAttach(): Unit = {
    super.afterAttach()
    update()
  }

  /**
    * Called to perform a detach operation -- ie, anything necessary to clean up the DOM node,
    * and then remove it from domNode so we know it's gone.
    */
  override def detach(): Unit = {
    // do nothing
  }

  override def update(): Unit = {
    for { e <- _domNode } {
      try {
        val d = data
        if (!lastData.contains(d)) {
          e.innerHTML = transform(d)
          lastData = Some(d)
        }
      } catch {
        case x:Throwable =>
          x.printStackTrace()
          e.innerHTML = "ERROR: " + x.getMessage
      }
    }
  }
}

object MarkupNode {


}
