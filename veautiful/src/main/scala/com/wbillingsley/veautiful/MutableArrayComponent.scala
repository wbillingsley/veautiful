package com.wbillingsley.veautiful

/**
  * A component that works a little more like d3.js in that it synchronises its children with a data array.
  *
  * There's no "key" function (as there is in d3) because it might make sense to have a MutableMapComponent for that.
  * Note that this is not aimed at being a full d3 replacement. (Just use d3 for that - it can be embedded in a VNode)
  */
class MutableArrayComponent[Container, AcceptChild, RealChild <: AcceptChild, Data](container: ParentNode[Container, AcceptChild], data: Array[Data])(
  onEnter: (Data, Int) => VNode[RealChild],
  onUpdate: (Data, Int, VNode[RealChild]) => Unit,
  onExit: (Data, Int, VNode[RealChild]) => Unit = { (_:Data, _:Int, _:VNode[RealChild]) => },
) extends VNode[Container] with Update {

  override def domNode: Option[Container] = container.domNode

  /**
    * Called to perform an attach operation -- ie, create the real DOM node and put it into
    * domNode
    */
  override def attach(): Container = container.attach()

  /**
    * Called to perform a detach operation -- ie, anything necessary to clean up the DOM node,
    * and then remove it from domNode so we know it's gone.
    */
  override def detach(): Unit = {
    lastData = Seq.empty
    lastRendered = Array.empty
    container.detach()
  }

  private var lastRendered: Array[VNode[RealChild]] = Array.empty
  private var lastData: collection.Seq[Data] = Seq.empty[Data]

  override def update(): Unit = {

    val oldLength = lastRendered.length
    val newLength = data.length

    // The enter seq is new elements arriving in the array
    val entering = (oldLength until newLength)
    val exiting = (newLength until oldLength)

    for {
      ops <- container.nodeOps
    } {

      // Remove the exiting nodes
      for {
        i <- exiting
        v = lastRendered(i)
        d = lastData(i)
      } {
        onExit(d, i, v)
        v.beforeDetach()
        ops.removeAttachedChild(v)
        v.detach()
        v.afterDetach()
      }

      // Add the entering nodes
      val added = for {
        i <- entering
      } yield {
        val v = onEnter(data(i), i)
        v.beforeAttach()
        v.attach()
        ops.appendAttachedChild(v)
        v
      }

      val updateSeq = lastRendered ++ added

      for {
        i <- data.indices
      } {
        onUpdate(data(i), i, updateSeq(i))
      }

      lastRendered = updateSeq
      lastData = data
    }
  }
}

object MutableArrayComponent {


}
