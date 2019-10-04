package com.wbillingsley.veautiful
import org.scalajs.dom.Node

trait DiffComponent extends VNode with Update {

  protected def render:DiffNode

  var lastRendered:Option[DiffNode] = None

  def rerender():DiffNode = {
    val r = render
    lastRendered match {
      case Some(lr) => lr.makeItSo(r); lr
      case _ => lastRendered = Some(r); r
    }
  }

  def domNode: Option[Node] = lastRendered.flatMap(_.domNode)

  def update(): Unit = rerender()

  /**
    * Called to perform an attach operation -- ie, create the real DOM node and put it into
    * domNode
    */
  override def attach(): Node = lastRendered.getOrElse(rerender()).attach()

  /**
    * Called to perform a detach operation -- ie, anything necessary to clean up the DOM node,
    * and then remove it from domNode so we know it's gone.
    */
  override def detach(): Unit = lastRendered.foreach(_.detach())

}

