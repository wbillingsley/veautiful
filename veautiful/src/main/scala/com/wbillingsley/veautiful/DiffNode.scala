package com.wbillingsley.veautiful

import com.wbillingsley.veautiful.reconcilers.Reconciler
import org.scalajs.dom

import scala.collection.mutable

/**
  * A DiffNode is a node in the view tree that can mutate itself to match an instruction - normally a destination node.
  * 
  * It's called a diff node because effectively it "diffs" its children in order to work out how to mutate its current 
  * child list into the new child list. This allows React.js-like operation.
  * 
  * The implementation of MakeItSo is left to subclasses, as some might wish to accept `Blueprint`s as well as rendered
  * nodes.
  */
trait DiffNode[+N, C] extends ParentNode[N, C] with MakeItSo {

  /** How children should be reconciled. This is left undefined, as some child classes may wish to make it settable. */
  def reconciler:Reconciler

}
