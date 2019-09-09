package example

import scala.util.Random

/**
  * A Vec2 contains an x and a y value
  *
  * This could represent an (x,y) position, or an (vx, vy) velocity,
  * or an (ax, ay) acceleration
  *
  * Vector addition and subtraction are implemented for you.
  * As is multiplication by a scalar (by a number)
  *
  * Also implemented is converting to and from "r, theta" notation where
  * instead of an x and y value, you have a magnitude and an angle.
  *
  * You might find reading the test spec useful for examples of how to call these
  * functions
  *
  * @param x
  * @param y
  */
case class Vec2(x:Double, y:Double) {

  /** Vector addition -- adds this Vec2 to another */
  def +(v:Vec2) = Vec2(x + v.x, y + v.y)

  /** Vector subtraction -- subtracts the other vector from this one */
  def -(v:Vec2) = Vec2(x - v.x, y - v.y)

  /**
    * Returns the angle this vector makes from the origin.
    * Imagine this vector is an arrow on graph-paper from (0,0) to (x,y).
    * Theta is the angle from the x axis.
    */
  def theta:Double = Math.atan2(y, x)

  /**
    * Returns the magnitude of this vector
    * Imagine this vector is an arrow on graph-paper from (0,0) to (x,y).
    * magnitude is the length of the arrow
    * @return
    */
  def magnitude:Double = Math.sqrt(Math.pow(x,2) + Math.pow(y,2))

  /**
    * Multiplication by a scalar. Multiplies the x and y values by d.
    * eg, Vec2(7,9) * 2 == Vec2(14, 18)
    */
  def *(d:Double) = Vec2(x * d, y * d)


  /**
    * Division by a scalar. Divides the x and y values by d.
    * eg, Vec2(14, 18) / 2 == Vec2(7, 9)
    */
  def /(d:Double):Vec2 = *(1/d)

  /**
    * Returns a vector that has the same angle (theta) but a magnitude (arrow length)
    * of 1
    * @return
    */
  def normalised:Vec2 = Vec2.fromRTheta(1, theta)

  /**
    * If the magnitude is greater than mag, returns a vector that has the same angle
    * (theta) but a magnitude (arrow length) of mag.
    *
    * Otherwise, if this vector is shorter than mag, just returns this vector.
    * @param mag
    * @return
    */
  def limit(mag:Double):Vec2 = {
    if (magnitude > mag) {
      Vec2.fromRTheta(mag, theta)
    } else this
  }

}

/**
  * Companion object for Vec2. Contains functions for creating Vec2s.
  */
object Vec2 {

  /**
    * Takes an angle (theta) and a length (r), and returns a Vec2 representing that
    * arrow. Note that theta is measured in radians (there are 2 * PI radians in a
    * circle rather than 360 degrees in a circle).
    *
    * @param r
    * @param theta
    * @return
    */
  def fromRTheta(r:Double, theta:Double):Vec2 = {
    Vec2(r * Math.cos(theta), r * Math.sin(theta))
  }

  /** A vector of magnitude d in a random direction */
  def randomDir(d:Double):Vec2 = {
    val theta = Random.nextDouble() * Math.PI * 2
    Vec2.fromRTheta(d, theta)
  }

  val E:Double = 0
  val SE:Double = Math.PI / 4
  val S:Double = Math.PI / 2
  val SW:Double = 3 * Math.PI / 4
  val W:Double = Math.PI
  val NW:Double = 5 * Math.PI / 4
  val N:Double = 3 * Math.PI / 2
  val NE:Double = 7 * Math.PI / 4


}
