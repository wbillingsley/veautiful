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

  def upd(to:Seq[VNode]) = {

    // 

  }

  def updateChildren(to:Seq[VNode]):Unit = {
    val diffOps = Differ.seqDiff(children, to)

    val updating = children.toBuffer

    def insertDomNode(i:Int, childN:dom.Node, parentN:dom.Node): Unit = {
      if (i >= parentN.childNodes.length - 1) {
        parentN.appendChild(childN)
      } else {
        parentN.insertBefore(childN, parentN.childNodes(i))
      }
    }

    for {
      n <- domNode
      op <- diffOps
    } op match {
      case Differ.LInsert(i, node) =>
        node.beforeAttach()
        insertDomNode(i, node.attach(), n)
        updating.insert(i, node)
        node.afterAttach()

      case Differ.LRemove(i) =>
        val child = updating(i)
        if (!child.isAttached) {
          throw new IllegalStateException("Child being removed is not attached")
        } else {
          for {
            childN <- child.domNode
          } {
            child.beforeDetach()
            n.removeChild(childN)
            child.detach()
            child.afterDetach()
            updating.remove(i)
          }
        }

      case Differ.LMove(fromI, toI) =>
        val child = updating(fromI)
        if (!child.isAttached) {
          throw new IllegalStateException("Child being removed is not attached")
        } else {
          for {
            childN <- child.domNode
          } {
            n.removeChild(childN)
            updating.remove(fromI)
            insertDomNode(toI, childN, n)
            updating.insert(toI, child)
          }
        }
    }

    updating.zip(to).collect({ case (x:MakeItSo, y:MakeItSo) => x.makeItSo(y) })

    /*
    for { (c, i) <- children.zipWithIndex } {
      if (c.domNode != domNode.map(_.childNodes(i))) {
        println(
          s"""
             | MISMATCH
             | ${c.domNode}
             | ${domNode.map(_.childNodes(i))}
           """.stripMargin)
      }
    }*/

    children = updating.toVector
  }
}
