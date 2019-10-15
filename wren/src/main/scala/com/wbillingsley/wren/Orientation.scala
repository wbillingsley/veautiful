package com.wbillingsley.wren

sealed trait Orientation {
  val rotationMatrix:((Int, Int),
                      (Int, Int))

  /**
    * Rotates the location clockwise from East, remembering that +y is down the page.
    * @param pos
    * @param around
    * @return
    */
  def rotate(pos:(Int, Int), around:(Int, Int) = (0, 0)):(Int, Int) = {
    val (x, y) = pos
    val (ox, oy) = around
    val (rx, ry) = (ox - x, oy - y)

    val (
      (m00, m01),
      (m10, m11)
    ) = rotationMatrix

    (m00 * rx + m01 * ry + ox, m10 * rx + m11 * ry + oy)
  }
}
object Orientation {
  case object East extends Orientation {
    override val rotationMatrix = (
      (1, 0),
      (0, 1)
    )
  }

  case object South extends Orientation {
    override val rotationMatrix = (
      (0, -1),
      (1, 0)
    )
  }

  case object West extends Orientation {
    override val rotationMatrix = (
      (-1, 0),
      (0, -1)
    )
  }

  case object North extends Orientation {
    override val rotationMatrix = (
      (0, 1),
      (-1, 0)
    )
  }

}
