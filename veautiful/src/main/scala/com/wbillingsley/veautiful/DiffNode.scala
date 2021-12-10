package com.wbillingsley.veautiful

import com.wbillingsley.veautiful.reconcilers.Reconciler
import org.scalajs.dom

import scala.collection.mutable

/**
  * A DiffNode is a node in the view tree that can mutate itself to match a destination node.
  * It's called a diff node because effectively it "diffs" its children in order to work out how to
  * mutate its current child list into the new child list.
  *
  * This allows React.js-like operation.
  */
trait DiffNode[+N, C] extends DNode[N, C] with MakeItSo {

  /** How children should be reconciled. This is left undefined, as some child classes may wish to make it settable. */
  def reconciler:Reconciler

  /**
    * The children of a DiffNode has to be mutable, as it mutates itself to become the destination
    */
  var children:collection.Seq[VNode[C]] = Vector.empty

  def updateSelf:PartialFunction[DiffNode[_, C], _]

  def makeItSo:PartialFunction[MakeItSo, _] = { case to:DiffNode[N, C] =>
    updateSelf(to)
    updateChildren(to.children)
  }

  def updateChildren(to:collection.Seq[VNode[C]]):Unit = {
    reconciler.updateChildren(this, to)
  }
}
