package com.wbillingsley.veautiful

import scala.annotation.tailrec
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

object Differ {

  sealed trait LOp[T]
  case class LMove[T](from:Int, to:Int) extends LOp[T]
  case class LRemove[T](from:Int) extends LOp[T]
  case class LInsert[T](to:Int, value:T) extends LOp[T]

  /*
   * This finds the next index at which the nodes are different
   */
  def nextDiff[T](leftI:Int, left:Seq[T], rightI:Int, right:Seq[T]):(Int, Int) = {

    var l = leftI
    var r = rightI
    val ll = left.length
    val rl = right.length

    while (l < ll && r < rl && left(l) == right(r)) {
      l += 1
      r += 1
    }

    (l, r)
  }

  def seqDiff[T](before:Seq[T], after:Seq[T]):Seq[LOp[T]] = {
    /* These nodes are common to the old state and the new state, and will be kept */
    val retain = before.toBuffer

    /* we'll collect our operations here */
    val ops = Seq.empty[LOp[T]].toBuffer


    /*
     * Next we need to step through the target list, with cursors for the current and new nodes, setting their positions
     */
    var retainCursor = 0
    var targetCursor = 0

    // We're done if we've retained all the nodes and added all the nodes
    def targetDone = targetCursor >= after.length

    def moveHere(from:Int):Unit = {
      ops.append(LMove(from, retainCursor))
      val item = retain(from)
      retain.remove(from)
      retain.insert(retainCursor, item)
      retainCursor += 1
      targetCursor += 1
    }

    // Inserts a node here. Does not call attach or afterAttach
    def insertHere(addIndex:Int) = {
      val item = after(addIndex)
      ops.append(LInsert(retainCursor, item))
      retain.insert(retainCursor, item)
      retainCursor += 1
      targetCursor += 1
    }

    while (!targetDone) {
      val (nr, nt) = nextDiff(retainCursor, retain, targetCursor, after)
      retainCursor = nr
      targetCursor = nt

      if (!targetDone) {
        // We need to make the retain match the target. The node could be later in the retain set (if a node has
        // moved up) or it could be in the add set. Try the add set first, as it will often be smaller

        val templateToInsert = after(targetCursor)

        // Look in the retain set first
        val inRetain = retain.indexOf(templateToInsert, retainCursor)
        if (inRetain >= 0) {
          moveHere(inRetain)
        } else {
          insertHere(targetCursor)
        }
      }
    }

    /* anything now left over on the left hand side is due for removal */
    ops.append({
      (for { i <- retainCursor until retain.length } yield {
        LRemove[T](i)
      }).reverse
    }:_*)

    ops
  }

  sealed trait MOp[K, V]
  case class MRemove[K,V](key:K) extends MOp[K, V]
  case class MInsert[K,V](key:K, value:V) extends MOp[K, V]

  def mapDiff[K, V](from:Map[K, V], to:Map[K, V]):Seq[MOp[K, V]] = {

    Seq.empty
  }

}

trait Keyable {
  def key: Option[Any] = None
}

object NewDiffer {

  sealed trait DiffOp[+K]
  case object Retain extends DiffOp[Nothing]
  case class Remove[K](r: K) extends DiffOp[K]
  case class Append[K](r: K) extends DiffOp[K]
  case class Replace[K](w: K) extends DiffOp[K]
  case class InsertBefore[K](item:K, before: K) extends DiffOp[K]

  sealed trait Strategy
  case object InsertBeforeStrat extends Strategy
  case object RemoveStrat extends Strategy

  def diffs[K <: Keyable](left:Seq[K], right:Seq[K]): Seq[DiffOp[K]] = {

    val ops = ArrayBuffer.empty[DiffOp[K]]

    val create = ArrayBuffer.empty[K]
    val remove = ArrayBuffer.empty[K]

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
      r <- rightIt
    } {
      var done = false

      do {
        if (leftIt.isEmpty) {
          ops.append(Append(r))
          create.append(r)
          done = true
        } else {
          val l = leftIt.next()
          if (l == r) {                 // The common case will be the item will already be there
            ops.append(Retain)
            done = true
          } else if (!movedUp(l)) {     // If we're looking at a left item that's moved up the list, skip it

            r.key.flatMap(leftKeys.get) match { // Try to find it in the left keys and move it here
              case Some(item) =>
                ops.append(InsertBefore(item, l))
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

    ops
  }





}