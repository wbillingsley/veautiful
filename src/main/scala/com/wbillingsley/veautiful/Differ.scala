package com.wbillingsley.veautiful

object Differ {

  sealed trait LOp[T]
  case class LMove[T](from:Int, to:Int) extends LOp[T]
  case class LRemove[T](from:Int) extends LOp[T]
  case class LInsert[T](to:Int, value:T) extends LOp[T]

  /*
   * This finds the next index at which the nodes are different
   */
  def nextDiff[T](leftI:Int, left:Seq[T], rightI:Int, right:Seq[T]):(Int, Int) = {

    var l = leftI;
    var r = rightI;
    while (l < left.length && r < right.length && left(l) == right(r)) {
      l += 1
      r += 1
    }

    (l, r)
  }

  def seqDiff[T](before:Seq[T], after:Seq[T]):Seq[LOp[T]] = {
    /* These nodes are common to the old state and the new state, and will be kept */
    val retain = before.toBuffer
    val afterB = after.toBuffer

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
      val item = afterB(addIndex)
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
        val inRetain = retain.view(retainCursor, retain.length).indexOf(templateToInsert)
        if (inRetain >= 0) {
          moveHere(inRetain + retainCursor)
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
