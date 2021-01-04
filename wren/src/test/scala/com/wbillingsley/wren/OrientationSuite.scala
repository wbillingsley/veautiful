package com.wbillingsley.wren

class OrientationSuite extends munit.FunSuite {

  test("Orientation should rotate (1,0) to the south") {
    assert(Orientation.South.rotate((2,0), (0,0)) == (0, 2))
  }


}
