package com.wbillingsley.veautiful
import org.scalajs.dom.{Element, Node}

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

  def domNode: Option[Element] = lastRendered.flatMap(_.domNode)

  def update(): Unit = rerender()

  def delegate:DiffNode = lastRendered.getOrElse(rerender())

  override def beforeAttach(): Unit = {
    super.beforeAttach()
    delegate.beforeAttach()
  }

  override def attach(): Node = delegate.attach()

  override def afterAttach(): Unit = {
    super.afterAttach()
    delegate.afterAttach()
  }

  override def beforeDetach(): Unit = lastRendered.foreach(_.beforeDetach())
  override def detach(): Unit = lastRendered.foreach(_.detach())
  override def afterDetach(): Unit = lastRendered.foreach(_.afterDetach())

}

