package com.wbillingsley.veautiful
import org.scalajs.dom.{Element, Node}

trait DiffComponent[N, C] extends VNode[N] with Update {

  protected def render:DiffNode[N, C]

  var lastRendered:Option[DiffNode[N, C]] = None

  def rerender():DiffNode[N, C] = {
    val r = render
    lastRendered match {
      case Some(lr) => lr.makeItSo(r); lr
      case _ => lastRendered = Some(r); r
    }
  }

  def domNode: Option[Element] = lastRendered.flatMap(_.domNode)

  def update(): Unit = rerender()

  def delegate:DiffNode[N, C] = lastRendered.getOrElse(rerender())

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

