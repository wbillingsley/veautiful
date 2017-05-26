package example

import com.wbillingsley.veautiful._
import example.Model.Asteroid

/**
  * Created by wbilling on 26/05/2017.
  */
object ReactLike {

  import Model._

  /**
    * The SVG that will contain the asteroid field
    */
  def svg:DElement = <.svg.attrs(
    ^.attr("width") := "640",
    ^.attr("height") := "480"
  )

  /** Turns an asteroid into an SVG DElement */
  def svgAsteroid(a:Asteroid) = {

    /** Just defines the shape of an asteroid */
    def polyPoints:Seq[(Int, Int)] = Seq((-10, -2), (-5, 8), (0, 10), (4, 7), (10, -1), (0, -10))

    /** Useful for turning a point into a string */
    def pointToString(p:(Int,Int)) = s"${p._1},${p._2} "

    /** Formats a polygon's SVG point string */
    def polyString(s:Seq[(Int, Int)]):String = {
      val sb = new StringBuilder()
      for { point <- s } sb.append(pointToString(point))
      sb.mkString
    }

    val (x, y) = a.pos
    val points = for {
      p <- polyPoints
    } yield (p._1 + a.pos._1.toInt, p._2 + a.pos._2.toInt)

    <.polygon.attrs(^.attr("points") := polyString(points), ^.cls := "asteroid")

  }

  /** Creates an SVG for a gravity well */
  def svgWell(w:Well) = {
    val (x, y) = w.pos

    <.circle.attrs(
      ^.cls := "well",
      ^.attr("cx") := x, ^.attr("cy") := y, ^.attr("r") := w.radius
    )
  }

  def reactUI:VNode = Common.layout(
    <.div.children(
      <.p(
        """
          | This version of the UI works most closely to how React.js (or at least
          | scala-js-react) does.
        """.stripMargin
      ),
      SimulationView,
      <.p(
        "etc"
      )
    )
  )

  /**
    * This is the view component.
    */
  case object SimulationView extends ElementComponent(<.div(^.cls := "boo")) {

    override def afterAttach() = {
      super.afterAttach()
      println("attaching")
      Model.addListener(rerender)
    }

    override def attach() = {
      println("Attaching")
      super.attach()
    }

    override def beforeDetach() = Model.removeListener(rerender)

    var last:Long = System.currentTimeMillis()
    var dt:Long = 0

    renderElements(<.p("Not yet ready"))

    def rerender():Unit = {
      println("SimulationView Rerender")
      val now = System.currentTimeMillis()
      dt = now - last
      last = now

      renderElements(card(Model.asteroids))
    }

    def card(asteroids:Seq[Asteroid]) = {
      <.div(^.cls := "card",
        svg.children(
          Model.wells.map(svgWell) ++ Model.asteroids.map(svgAsteroid) :_*
        ),
        <.div(^.cls := "card-footer",
          <.p(s"${asteroids.length} asteroids rendering in ${dt}ms"),
          <.div(^.cls := "btn-group",
            <("button")(
              ^.cls := "btn btn-secondary", ^.onClick --> Model.reset, "Reset"
            ),
            <("button")(
              ^.cls := "btn btn-secondary", ^.onClick --> Model.stopTicking(), "Stop"
            ),
            <("button")(
              ^.cls := "btn btn-secondary", ^.onClick --> Model.startTicking(), "Start"
            )
          )
        )
      )
    }
  }

}
