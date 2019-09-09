package com.wbillingsley.veautiful

import org.scalajs.dom

import scala.collection.mutable

/**
  * A DiffNode is a node in the view tree that can mutate itself to match a destination node.
  * It's called a diff node because effectively it "diffs" its children in order to work out how to
  * mutate its current child list into the new child list.
  *
  * This allows React.js-like operation.
  */
trait DiffNode extends DNode with MakeItSo {

  /**
    * The children of a DiffNode has to be mutable, as it mutates itself to become the destination
    */
  var children:Seq[VNode] = Vector.empty

  def updateSelf:PartialFunction[DiffNode, _]

  def makeItSo:PartialFunction[MakeItSo, _] = { case to:DiffNode =>
    updateSelf(to)
    updateChildren(to.children)
  }

  def updateChildren(to:Seq[VNode]):Unit = {

    val diffReport = Differ.diffs(children, to)
    Differ.processDiffs(this, diffReport.ops)
    children = diffReport.update

    // Now we recurse down the list
    children.iterator.zip(to.iterator) foreach {
      case (uu:MakeItSo, tt:MakeItSo) => uu.makeItSo(tt)
      case (u:Update, _) => u.update()
      case _ => // nothing to do
    }
  }
}
