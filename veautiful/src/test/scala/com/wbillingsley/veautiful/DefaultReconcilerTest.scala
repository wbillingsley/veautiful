package com.wbillingsley.veautiful

import org.scalatest._
import scala.util.Random

class DefaultReconcilerTest extends FlatSpec  {

  import com.wbillingsley.veautiful.reconcilers.Reconciler
  import Reconciler.default._

  // An item whose equality is determined by i but identity is determined by uid
  case class Item(i:Int) extends Keyable {
    val uid = Random.nextString(5)
  }

  "Default reconciler" should "Detect an appended item" in {
    val before = (1 to 5).map(Item)
    val after = (1 to 6).map(Item)

    val report = Reconciler.default.diffs(before, after)
    assert(report.ops == Seq(Append(Item(6))))
  }

  it should "result in an update set that matches (equality) the right list" in {
    val before = (1 to 5).map(Item)
    val after = (1 to 6).map(Item)

    val report = Reconciler.default.diffs(before, after)
    assert(report.update == after)
  }

  it should "result in an update set that retains (identity) the left list" in {
    val before = (1 to 5).map(Item)
    val after = (1 to 6).map(Item)

    val report = Reconciler.default.diffs(before, after)
    assert(report.update.zip(before).forall { case (x, y) => x.uid == y.uid })
  }




}
