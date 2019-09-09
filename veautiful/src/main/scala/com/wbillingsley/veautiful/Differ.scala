package com.wbillingsley.veautiful

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

trait Keyable {
  def key: Option[Any] = None
}

object Differ {

  sealed trait DiffOp[+K]
  case object Retain extends DiffOp[Nothing]
  case class Remove[K](r: K) extends DiffOp[K]
  case class Append[K](r: K) extends DiffOp[K]
  case class InsertBefore[K](item:K, before: K) extends DiffOp[K]

  sealed trait Strategy
  case object InsertBeforeStrat extends Strategy
  case object RemoveStrat extends Strategy

  /**
    * Report produced by the Differ
    *
    * @param exit Nodes that will be removed
    * @param enter Nodes that will be newly created
    * @param update Nodes that will be retained or created (matches the intended end result)
    * @param ops Operations to perform
    * @tparam K
    */
  case class DiffReport[K](exit:Seq[K], enter:Seq[K], update:Seq[K], ops:Seq[DiffOp[K]])

  /**
    * Generates a difference report between the two sets of nodes
    * @return
    */
  def diffs[K <: Keyable](left:Seq[K], right:Seq[K]): DiffReport[K] = {

    val ops = ArrayBuffer.empty[DiffOp[K]]

    val create = ArrayBuffer.empty[K]
    val remove = ArrayBuffer.empty[K]
    val update = ArrayBuffer(right:_*)

    val movedUpSet = mutable.Set.empty[Any]

    val leftKeys = (for {
      t <- left
      k <- t.key
    } yield (k -> t)).toMap

    val rightKeys = (for {
      t <- right
      k <- t.key
    } yield (k -> t)).toMap

    val leftIt = left.iterator
    val rightIt = right.iterator

    def clearRemaining() = {
      ops.appendAll(leftIt.map(Remove.apply))
    }

    def appendRemaining() = {
      ops.appendAll(rightIt.map(Append.apply))
    }

    def movedUp(i:K) = i.key.exists(movedUpSet.contains)

    for {
      (r, index) <- rightIt.zipWithIndex
    } {
      var done = false

      do {
        if (leftIt.isEmpty) {
          ops.append(Append(r))
          update(index) = r
          create.append(r)
          done = true
        } else {
          val l = leftIt.next()
          if (l == r) {                 // The common case will be the item will already be there
            //ops.append(Retain)
            update(index) = l           // We retain the old item
            done = true
          } else if (!movedUp(l)) {     // If we're looking at a left item that's moved up the list, skip it

            r.key.flatMap(leftKeys.get) match { // Try to find it in the left keys and move it here
              case Some(item) =>
                ops.append(InsertBefore(item, l))
                update(index) = item            // The found item should sit at this point in the update array
                movedUpSet.add(item.key.get)
                done = true

                l.key.flatMap(rightKeys.get) match {  // We need to work out what's happening to the left item
                  case Some(k) =>
                    println(s"item $k moving down")
                  // Just skip it - it'll be moving down later
                  case _ =>
                    // Remove this element and loop
                    ops.append(Remove(l))
                    remove.append(l)
                }

              case _ => // Didn't find it.
                l.key.flatMap(rightKeys.get) match {
                  case Some(k) =>
                    println(s"item $k moving down")
                    // Just skip it - it'll be moving down later
                  case _ =>
                    // Remove this element and loop
                    ops.append(Remove(l))
                    remove.append(l)
                }
            }

          }

        }

      } while (!done)

    }

    // Anything still in the iterator on the left hand side that has not "moved up" needs removing
    for { l <- leftIt if !movedUp(l) } {
      ops.append(Remove(l))
      remove.append(l)
    }

    DiffReport(
      exit = remove,
      enter = create,
      update = update,
      ops = ops
    )
  }

  def processDiffs(parent:DNode, ops:Seq[DiffOp[VNode]]):Unit = {

    for {
      p <- parent.domNode
      op <- ops
    } op match {
      case Retain => // do nothing

      case Remove(n) =>
        // TODO: check child was attached
        for { c <- n.domNode } {
          n.beforeDetach()
          p.removeChild(c)
          n.detach()
          n.afterDetach()
        }

      case Append(n) =>
        n.domNode match {
          case Some(c) => p.appendChild(c)
          case _ =>
            n.beforeAttach()
            p.appendChild(n.attach())
            n.afterAttach()
        }

      case InsertBefore(n, before) =>
        if (before.isAttached) {
          for { b <- before.domNode } {
            n.domNode match {
              case Some(c) => p.insertBefore(c, b)
              case _ =>
                n.beforeAttach()
                p.insertBefore(n.attach(), b)
                n.afterAttach()
            }
          }
        } else throw new IllegalStateException("Can't insert before - node to insert before is not attached")
    }


  }




}