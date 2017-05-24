package com.wbillingsley.veautiful

import utest._

object DifferTest extends TestSuite with App {

  import Differ._

  println("It's " + seqDiff(List(1, 2, 3, 4, 5, 6), List(6, 2, 4)))

  def tests = TestSuite {
    'Differ {

      assert(seqDiff(List(1, 2, 3), List(1, 2)) == Seq(LRemove[Int](2)))
    }
  }

}
