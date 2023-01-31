package com.wbillingsley.veautiful

import scala.collection.mutable

/**
  * A component that works a little more like d3.js in that it synchronises its children with a mutable map (likely a JavaScript hashmap)
  */
class MutableMapComponent[Container, AcceptChild, VChild <: VNode[AcceptChild], K, V](container: ParentNode[Container, AcceptChild], data: mutable.Map[K, V])(
  onEnter: (K, V) => VChild | Blueprint[VChild],
  onUpdate: (K, V, VChild) => Unit = { (_:K, _:V, _:VChild) => () },
  onExit: (K, VChild) => Unit = { (_:K, _:VChild) => },
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
    lastRendered = collection.Map.empty
    container.detach()
  }

  private var lastRendered: collection.Map[K, VChild] = collection.Map.empty

  /** Enables a builder-like syntax, where we can take a component and call .onEnter, .onUpdate, etc, to specialise it */
  def onEnter(onEnter:(K, V) => VChild | Blueprint[VChild]):MutableMapComponent[Container, AcceptChild, VChild, K, V] = 
    MutableMapComponent(container, data)(onEnter, onUpdate, onExit)

  /** Enables a builder-like syntax, where we can take a component and call .onEnter, .onUpdate, etc, to specialise it */
  def onUpdate(onUpdate:(K, V, VChild) => Unit):MutableMapComponent[Container, AcceptChild, VChild, K, V] = 
    MutableMapComponent(container, data)(onEnter=onEnter, onUpdate, onExit)

  /** Enables a builder-like syntax, where we can take a component and call .onEnter, .onUpdate, etc, to specialise it */
  def onExit(onExit:(K, VChild) => Unit):MutableMapComponent[Container, AcceptChild, VChild, K, V] = 
    MutableMapComponent(container, data)(onEnter, onUpdate, onExit)


  override def update(): Unit = {

    val oldKeys = lastRendered.keySet
    val newKeys = data.keySet

    val entering = newKeys diff oldKeys
    val exiting = oldKeys diff newKeys

    for {
      ops <- container.nodeOps
    } {

      // Remove the exiting nodes
      for {
        k <- exiting
        node = lastRendered(k)
      } {
        onExit(k, node)
        node.beforeDetach()
        ops.removeAttachedChild(node)
        node.detach()
        node.afterDetach()
      }

      // Add the entering nodes
      val added:Map[K, VChild] = (for {
        k <- entering
      } yield {
        val v:VChild = onEnter(k, data(k)) match {
          case b:Blueprint[VChild] @unchecked => b.build()
          case v:VChild @unchecked => v
        }
        v.beforeAttach()
        v.attach()
        ops.appendAttachedChild(v)
        k -> v
      }).toMap

      val updateMap:Map[K, VChild] = (
        for k <- data.keySet yield 
            k -> (if lastRendered.contains(k) then lastRendered(k) else added(k))
        ).toMap

      for {
        (k, v) <- data
      } {
        onUpdate(k, v, updateMap(k))
      }

      lastRendered = updateMap
    }
  }
}

object MutableMapComponent {

  extension [P, C] (parent:ParentNode[P, C]) {
    def generateChildren[K, V, VChild <: VNode[C]](map:mutable.Map[K, V])(
      onEnter: (K, V) => VChild | Blueprint[VChild]
    ):MutableMapComponent[P, C, VChild, K, V] = 
        MutableMapComponent(parent, map)(onEnter)
  }

  extension [P, C] (parent:Blueprint[ParentNode[P, C]]) {
    def generateChildren[K, V, VChild <: VNode[C]](data:mutable.Map[K, V])(
      onEnter: (K, V) => VChild | Blueprint[VChild]
    ):MutableMapComponent[P, C, VChild, K, V] = 
        MutableMapComponent(parent.build(), data)(onEnter)
  }

}
