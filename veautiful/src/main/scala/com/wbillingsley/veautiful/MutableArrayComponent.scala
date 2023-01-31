package com.wbillingsley.veautiful

/**
  * A component that works a little more like d3.js in that it synchronises its children with a data array.
  *
  * There's no "key" function (as there is in d3) because it might make sense to have a MutableMapComponent for that.
  * Note that this is not aimed at being a full d3 replacement. (Just use d3 for that - it can be embedded in a VNode)
  */
class MutableArrayComponent[Container, AcceptChild, VChild <: VNode[AcceptChild], Data](container: ParentNode[Container, AcceptChild], data: Array[Data])(
  onEnter: (Data, Int) => VChild | Blueprint[VChild],
  onUpdate: (Data, Int, VChild) => Unit = { (_:Data, _:Int, _:VChild) => () },
  onExit: (Int, VChild) => Unit = { (_:Int, _:VChild) => },
) extends VNode[Container] with Update {
  import scala.collection.mutable

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
    lastRendered = collection.Seq.empty
    container.detach()
  }

  private var lastRendered: collection.Seq[VChild] = collection.Seq.empty

  /** Enables a builder-like syntax, where we can take a component and call .onEnter, .onUpdate, etc, to specialise it */
  def onEnter(onEnter:(Data, Int) => VChild | Blueprint[VChild]):MutableArrayComponent[Container, AcceptChild, VChild, Data] = 
    MutableArrayComponent(container, data)(onEnter, onUpdate, onExit)

  /** Enables a builder-like syntax, where we can take a component and call .onEnter, .onUpdate, etc, to specialise it */
  def onUpdate(onUpdate:(Data, Int, VChild) => Unit):MutableArrayComponent[Container, AcceptChild, VChild, Data] = 
    MutableArrayComponent(container, data)(onEnter=onEnter, onUpdate, onExit)

  /** Enables a builder-like syntax, where we can take a component and call .onEnter, .onUpdate, etc, to specialise it */
  def onExit(onExit:(Int, VChild) => Unit):MutableArrayComponent[Container, AcceptChild, VChild, Data] = 
    MutableArrayComponent(container, data)(onEnter, onUpdate, onExit)


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
      } {
        onExit(i, v)
        v.beforeDetach()
        ops.removeAttachedChild(v)
        v.detach()
        v.afterDetach()
      }

      // Add the entering nodes
      val added = for {
        i <- entering
      } yield {
        val v:VChild = onEnter(data(i), i) match {
          case b:Blueprint[VChild] @unchecked => b.build()
          case v:VChild @unchecked => v
        }
        v.beforeAttach()
        v.attach()
        ops.appendAttachedChild(v)
        v
      }

      val updateSeq = lastRendered.take(data.length) ++ added

      for {
        i <- data.indices
      } {
        onUpdate(data(i), i, updateSeq(i))
      }

      lastRendered = updateSeq
    }
  }
}

object MutableArrayComponent {

  extension [P, C] (parent:ParentNode[P, C]) {
    def generateChildren[Data, VChild <: VNode[C]](data:Array[Data])(
      onEnter: (Data, Int) => VChild | Blueprint[VChild]
    ):MutableArrayComponent[P, C, VChild, Data] = 
        MutableArrayComponent(parent, data)(onEnter)
  }

  extension [P, C] (parent:Blueprint[ParentNode[P, C]]) {
    def generateChildren[Data, VChild <: VNode[C]](data:Array[Data])(
      onEnter: (Data, Int) => VChild | Blueprint[VChild]
    ):MutableArrayComponent[P, C, VChild, Data] = 
        MutableArrayComponent(parent.build(), data)(onEnter)
  }

}
