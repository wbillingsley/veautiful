package com.wbillingsley.veautiful.reconcilers

import com.wbillingsley.veautiful.{DiffNode, VNode, Blueprint}

/**
  * A reconciler is how children of a DiffNode are handled.
  *
  * Various other front-end frameworks use different approaches.
  * For example, React "diffs" the effective DOM tree, comparing subtrees if "componentShouldUpdate".
  * d3.js looks for entering and exiting nodes, but does not necessarily change the order.
  *
  * By allowing different Reconcilers to be chosen at different points in the tree, we can enable different behaviours.
  */
trait Reconciler {
  def updateChildren[N, C](node:DiffNode[N, C], to:collection.Seq[VNode[C] | Blueprint[VNode[C]]]):collection.Seq[VNode[C]]
}

object Reconciler {

  /** The default reconciler works similarly to an impure React component */
  val default = new DefaultReconciler(true)

  /** Only reconciles children if b evaluates to true. Rough equivalent of shouldComponentUpdate */
  def onlyIf(b: => Boolean) = new DefaultReconciler(b)

}