package example

import scala.collection.mutable
import scala.scalajs.js.timers
import scala.scalajs.js.timers.SetIntervalHandle

object Model {

  type Vec = (Double, Double)
  type Velocity = Vec
  type Position = Vec

  val w = 640

  val h = 480

  var drag = 0.99

  var count = 7;

  implicit class VecOps(val v:Vec) extends AnyVal {
    def *(d:Double):Vec = (v._1 * d, v._2 * d)
    def /(d:Double):Vec = (v._1 / d, v._2 / d)
    def +(o:Vec):Vec = (v._1 + o._1, v._2 + o._2)
    def -(o:Vec):Vec = (v._1 - o._1, v._2 - o._2)

    def magnitude:Double = Math.sqrt(Math.pow(v._1, 2) + Math.pow(v._2, 2))
    def theta:Double = Math.atan2(v._2, v._1)

    def toCartesian = (Math.cos(v._2) * v._1, Math.sin(v._2) * v._1)
    def toPolar = (v.magnitude, v.theta)
  }

  case class Asteroid(pos:Vec, velocity:Vec, radius:Double) {

    def update(acceleration:Vec, dt:Double) = {
      this.copy(
        pos = pos + (velocity * dt),
        velocity = (velocity * drag) + (acceleration * dt),
        radius
      )
    }

  }

  case class Well(pos:Vec, radius:Double, strength:Double) {

    def applyForce(asteroid:Asteroid):Vec = {
      val (r, theta) = (pos - asteroid.pos).toPolar

      val calcForce = strength / Math.pow(r, 2)
      val cappedForce = Math.min(calcForce, 1000)
      (cappedForce, theta).toCartesian
    }

  }

  var asteroids:Seq[Asteroid] = for {
    i <- 1 to count
  } yield randomAsteroid

  val wells:Seq[Well] = for {
    i <- 1 to 3
  } yield Well(((Math.random() * .6 + 0.2) * w, (Math.random() * .6 + .2 )* h), 10, 50000000)

  def randomAsteroid = {
    val x = Math.random() * w
    val y = Math.random() * h

    val pos = (x, y)
    val (r, theta) = (pos - (w/2, h/2)).toPolar
    val v = (r, theta + Math.PI/2).toCartesian
    Asteroid(pos, v, 10)
  }

  def reset():Unit = {
    asteroids = for {
      i <- 1 to count
    } yield randomAsteroid

    notifyListeners()
  }


  def update(dt:Double) = {
    val now = System.currentTimeMillis()

    asteroids = for {
      a <- asteroids
    } yield {
      val forces = wells.map(_.applyForce(a)).fold((0.0, 0.0))(_ + _)
      a.update(forces, dt)
    }

  }

  val listeners:mutable.Buffer[() => Unit] = mutable.Buffer.empty

  def addListener(f:() => Unit):Unit = listeners.append(f)

  def removeListener(f:() => Unit):Unit = listeners -= f

  def notifyListeners() = listeners.foreach(_())

  var handle:Option[SetIntervalHandle] = None

  def startTicking(): Unit = {
    def handleTick() = {
      val now = System.currentTimeMillis()
      val dt = 14 //System.currentTimeMillis() - last

      Model.update(dt / 1000.0)
      notifyListeners()
    }

    handle = handle orElse Some(timers.setInterval(14)(handleTick))
  }

  def stopTicking():Unit = {
    handle.foreach(timers.clearInterval)
    handle = None
  }


}
