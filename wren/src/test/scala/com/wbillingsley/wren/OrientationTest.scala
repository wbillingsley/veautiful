package com.wbillingsley.wren

import org.scalatest.FlatSpec

class OrientationTest extends FlatSpec {

  "Orientation" should "rotate (1,0) to the south" in {
    assert(Orientation.South.rotate((2,0), (0,0)) == (0, 2))
  }


}
