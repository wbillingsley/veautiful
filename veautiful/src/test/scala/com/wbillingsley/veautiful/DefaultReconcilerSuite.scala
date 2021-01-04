package com.wbillingsley.veautiful

import munit.FunSuite
import scala.util.Random

class DefaultReconcilerSuite extends FunSuite  {

  import com.wbillingsley.veautiful.reconcilers.Reconciler
  import Reconciler.default._

  // An item whose equality is determined by i but identity is determined by uid
  case class Item(i:Int) extends Keyable {
    val uid = Random.nextString(5)
  }

  test("Default reconciler should detect an appended item") {
    val before = (1 to 5).map(Item)
    val after = (1 to 6).map(Item)

    val report = Reconciler.default.diffs(before, after)
    assert(report.ops == Seq(Append(Item(6))))
  }

  test("Default reconciler should result in an update set that matches (equality) the right list") {
    val before = (1 to 5).map(Item)
    val after = (1 to 6).map(Item)

    val report = Reconciler.default.diffs(before, after)
    assert(report.update == after)
  }

  test("Default reconciler should result in an update set that retains (identity) the left list") {
    val before = (1 to 5).map(Item)
    val after = (1 to 6).map(Item)

    val report = Reconciler.default.diffs(before, after)
    assert(report.update.zip(before).forall { case (x, y) => x.uid == y.uid })
  }

}
