package com.wbillingsley.veautiful.reconcilers

import com.wbillingsley.veautiful.logging.Logger
import com.wbillingsley.veautiful.{DNode, DiffNode, Keyable, MakeItSo, Update, VNode}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js

class DefaultReconciler(shouldUpdate: => Boolean) extends Reconciler {

  val logger:Logger = Logger.getLogger(this.getClass)

  sealed trait DiffOp[+K]
  case object Retain extends DiffOp[Nothing]
  case class Remove[K](r: K) extends DiffOp[K]
  case class Append[K](r: K) extends DiffOp[K]
  case class InsertBefore[K](item:K, before: K) extends DiffOp[K]

  /**
   * Report produced by the Differ
   *
   * @param exit Nodes that will be removed
   * @param enter Nodes that will be newly created
   * @param update Nodes that will be retained or created (matches the intended end result)
   * @param ops Operations to perform
   * @tparam K
   */
  case class DiffReport[K](exit:collection.Seq[K], enter:collection.Seq[K], update:collection.Seq[K], ops:collection.Seq[DiffOp[K]])

  /**
   * Generates a difference report between the two sets of nodes
   * @return
   */
  def diffs[K <: Keyable](left:collection.Seq[K], right:collection.Seq[K]): DiffReport[K] = {

    val ops = ArrayBuffer.empty[DiffOp[K]]

    val create = ArrayBuffer.empty[K]
    val remove = ArrayBuffer.empty[K]
    val update:js.WrappedArray[K] = right match {
      case a:js.WrappedArray[K] => a
      case _ => js.WrappedArray.from(right)
    }

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

      while {
        if (leftIt.isEmpty) {
          logger.trace("Left empty")

          // If the right element has a key, it's left might have "moved down" (been skipped for us to come across at the end)
          // If we can find it, "Append" it here.
          r.key.flatMap(leftKeys.get) match {
            case Some(k) =>
              logger.trace(s"Found an item that moved down")
              ops.append(Append(k))
              update(index) = k
              done = true

            case _ =>
              ops.append(Append(r))
              update(index) = r
              create.append(r)
              done = true
          }

        } else {
          val l = leftIt.next()
          if (l == r) { // The common case will be the item will already be there
            //ops.append(Retain)
            update(index) = l // We retain the old item
            done = true
          } else if (!movedUp(l)) { // If we're looking at a left item that's moved up the list, skip it

            r.key.flatMap(leftKeys.get) match { // Try to find it in the left keys and move it here
              case Some(item) =>
                ops.append(InsertBefore(item, l))
                update(index) = item // The found item should sit at this point in the update array
                movedUpSet.add(item.key.get)
                done = true

                l.key.flatMap(rightKeys.get) match { // We need to work out what's happening to the left item
                  case Some(k) =>
                    logger.trace(s"item $k moving down")
                  // Just skip it - it'll be moving down later
                  case _ =>
                    // Remove this element and loop
                    ops.append(Remove(l))
                    remove.append(l)
                }

              case _ => // Didn't find it.
                l.key.flatMap(rightKeys.get) match {
                  case Some(k) =>
                    logger.trace(s"item $k moving down")
                  // Just skip it - it'll be moving down later
                  case _ =>
                    // Remove this element and loop
                    ops.append(Remove(l))
                    remove.append(l)
                }
            }

          }

        }

        !done
      } do { } // Scala 3 do .. while  

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

  def processDiffs[N, C](parent:DNode[N, C], ops:collection.Seq[DiffOp[VNode[C]]]):Unit = {

    for {
      nodeOps <- parent.nodeOps
      op <- ops
    } op match {
      case Retain => // do nothing

      case Remove(n) =>
        // TODO: check child was attached
        if (n.isAttached && nodeOps.nodeIsChildOfMine(n)) {
          n.beforeDetach()
          nodeOps.removeAttachedChild(n)
          n.detach()
          n.afterDetach()
        }

      case Append(n) =>
        if (n.isAttached) {
          nodeOps.appendAttachedChild(n)
        } else {
          n.beforeAttach()
          n.attach()
          nodeOps.appendAttachedChild(n)
          n.afterAttach()
        }

      case InsertBefore(n, before) =>
        if (n.isAttached) {
          nodeOps.insertAttachedChildBefore(n, before)
        } else {
          n.beforeAttach()
          n.attach()
          nodeOps.insertAttachedChildBefore(n, before)
          n.afterAttach()
        }

    }
  }

  override def updateChildren[N, C](node: DiffNode[N, C], to: collection.Seq[VNode[C]]): Unit = {
    if (shouldUpdate) {
      if (node.children != to) {
        val diffReport = diffs(node.children, to)
        processDiffs(node, diffReport.ops)
        node.children = diffReport.update
      }

      // Now we recurse down the list
      node.children.iterator.zip(to.iterator) foreach {
        case (uu: MakeItSo, tt: MakeItSo) => uu.makeItSo(tt)
        case (u: Update, _) => u.update()
        case _ => // nothing to do
      }
    }
  }

}
