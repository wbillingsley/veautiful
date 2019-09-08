package com.wbillingsley.veautiful

import org.scalatest._

class NewDifferTest extends FunSpec {

  case class Num(i:Int) extends Keyable {
    override def key = Some(i)
  }

  val left = Seq(1, 2, 3, 4, 5, 6, 7, 8, 9).map(Num.apply)
  val right = Seq(1, 2, 3, 5, 6, 7, 8, 9).map(Num.apply)

  // Initialize App

  describe("NewDiffer") {
    it("should diff numbers") {
      assert(NewDiffer.diffs(left, right) == "hi")
    }
  }
}
