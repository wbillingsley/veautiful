package com.wbillingsley.veautiful

trait DiffComponent[N, C] extends VNode[N] with Update {

  protected def render:DiffNode[N, C] | Blueprint[DiffNode[N, C]]

  var lastRendered:Option[DiffNode[N, C]] = None

  def rerender():DiffNode[N, C] = {
    val r = render
    lastRendered match {
      case Some(lr) => lr.makeItSo(r); lr
      case _ =>  
        r match {
          // We shouldn't need to check these as they're the only two possibilities in the union type
          case d:DiffNode[N, C] @unchecked => 
            lastRendered = Some(d)
            d
          case b:Blueprint[DiffNode[N, C]] @unchecked =>
            val d = b.build()
            lastRendered = Some(d)
            d
        }
    }
  }

  def domNode: Option[N] = lastRendered.flatMap(_.domNode)

  def update(): Unit = rerender()

  def delegate:DiffNode[N, C] = lastRendered.getOrElse(rerender())

  override def beforeAttach(): Unit = {
    super.beforeAttach()
    for d <- lastRendered do d.beforeAttach()
  }

  override def attach(): N = delegate.attach()

  override def afterAttach(): Unit = {
    super.afterAttach()
    delegate.afterAttach()
  }

  override def beforeDetach(): Unit = lastRendered.foreach(_.beforeDetach())
  override def detach(): Unit = lastRendered.foreach(_.detach())
  override def afterDetach(): Unit = lastRendered.foreach(_.afterDetach())

}

