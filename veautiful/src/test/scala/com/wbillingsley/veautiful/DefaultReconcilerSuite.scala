package com.wbillingsley.veautiful

import munit.FunSuite
import scala.util.Random

class DefaultReconcilerSuite extends FunSuite  {

  import com.wbillingsley.veautiful.reconcilers.Reconciler
  import Reconciler.default._

  // An item whose equality is determined by i but identity is determined by uid
  case class CaseClassItem(i:Int) extends HasRetention {
    val uid = Random.nextString(5)
  }

  // An item whose equality is by reference, but implements the Keep strategy so that it is retained if i matches
  class KeepItem(i:Int) extends Keep(i)

  // An item with the Keep strategy but also with a morphing property
  class MorphingItem(i:Int)(p0:String) extends Keep(i) with Morphing(p0) {
    val morpher: MakeItSo = createMorpher(this)

    override def update():Unit = ()
  }

  test("Default reconciler should detect an appended case item") {
    val before = (1 to 5).map(CaseClassItem.apply)
    val after = (1 to 6).map(CaseClassItem.apply)

    val report = Reconciler.default.diffs(before, after)
    assert(report.ops == Seq(Append(CaseClassItem(6))))
  }

  test("Default reconciler should result in an update set that matches (equality) the right list") {
    val before = (1 to 5).map(CaseClassItem.apply)
    val after = (1 to 6).map(CaseClassItem.apply)

    val report = Reconciler.default.diffs(before, after)
    assert(report.update == after)
  }

  test("Default reconciler should result in an update set that retains (identity) for case class items in the left list") {
    val before = (1 to 5).map(CaseClassItem.apply)
    val after = (1 to 6).map(CaseClassItem.apply)

    val report = Reconciler.default.diffs(before, after)
    println(report.ops)
    assert(report.update.zip(before).forall { case (x, y) => x.uid == y.uid })
  }

  test("Default reconciler should result in an update set that retains (identity) for Keep items in the left list") {
    val before = (1 to 5).map(KeepItem(_))
    val after = (1 to 6).map(KeepItem(_))

    val report = Reconciler.default.diffs(before, after)
    assert(report.update.zip(before).forall { case (x, y) => x == y })
  }

  test("In the report before reconciliation, a Morphing Keep item should be retained") {
    val sourceMorph = MorphingItem(11)("Algernon")
    val before = List(CaseClassItem(1), CaseClassItem(2), sourceMorph, CaseClassItem(3), CaseClassItem(4))
    val destMorph = MorphingItem(11)("Bertie")
    val after = List(CaseClassItem(1), CaseClassItem(2), destMorph, CaseClassItem(3), CaseClassItem(4))

    val report = Reconciler.default.diffs(before, after)

    // The source Morphing Item should still be in the list, but its property should have updated
    assert(report.update.contains(sourceMorph))
  }


}
