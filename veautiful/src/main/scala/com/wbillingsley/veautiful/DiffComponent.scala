package com.wbillingsley.veautiful
import org.scalajs.dom.Node

trait DiffComponent extends VNode with Update {

  def render:DiffNode

  lazy val rendered = render

  def rerender():DiffNode = {
    rendered.makeItSo(render)
    rendered
  }

  def domNode: Option[Node] = rendered.domNode

  def update(): Unit = rerender()

  /**
    * Called to perform an attach operation -- ie, create the real DOM node and put it into
    * domNode
    */
  override def attach(): Node = rendered.attach()

  /**
    * Called to perform a detach operation -- ie, anything necessary to clean up the DOM node,
    * and then remove it from domNode so we know it's gone.
    */
  override def detach(): Unit = rendered.detach()

}

