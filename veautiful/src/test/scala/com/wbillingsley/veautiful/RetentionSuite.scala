package com.wbillingsley.veautiful

import munit.FunSuite
import scala.util.Random

class RetentionSuite extends FunSuite  {

  import com.wbillingsley.veautiful.reconcilers.Reconciler
  import Reconciler.default._

  // An item whose equality is determined by i but identity is determined by uid
  case class CaseClassItem(i:Int) extends HasRetention {
    val uid = Random.nextString(5)
  }

  // An item whose equality is by reference, but implements the Keep strategy so that it is retained if i matches
  class KeepItem(i:Int, prop0:String) extends Keep(i)

  test("A case class item will be retained using the default strategy if it matches by value") {
    assert(CaseClassItem(6).retainFor(CaseClassItem(6)) == true)
  }

  test("A case class item will not be retained using the default strategy if it does not match by value") {
    assert(CaseClassItem(6).retainFor(CaseClassItem(5)) == false)
  }

  test("A Keep item will be retained if it matches by identity") {
    val a = KeepItem(5, "five")
    assert(a.retainFor(a) == true)
  }

  test("A Keep item will be retained if it matches by Keep value") {
    assert(KeepItem(5, "a").retainFor(KeepItem(5, "b")) == true)
  }

  test("A Keep item will not be retained if it differs by Keep value") {
    assert(KeepItem(5, "a").retainFor(KeepItem(6, "a")) == false)
  }
}
