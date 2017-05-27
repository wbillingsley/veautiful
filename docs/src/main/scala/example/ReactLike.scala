package example

import com.wbillingsley.veautiful._
import example.Model.Asteroid
import org.scalajs.dom.raw.HTMLInputElement

/**
  * A version of the UI that works most closely to a React-style interface. The UI is
  * made up of elements, and the virtual DOM is diffed at each step.
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
  def svgAsteroid(a:Asteroid):VNode = {

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
  def svgWell(w:Well):VNode = {
    val (x, y) = w.pos

    <.circle(
      ^.cls := "well",
      ^.attr("cx") := x, ^.attr("cy") := y, ^.attr("r") := w.radius
    )
  }

  def page:VNode = Common.layout(
    <.div(
      <.h1("React-like rendering into an SVG"),
      <.p(
        """
          | In this version, the simulation is rendered with SVG elements. The UI is mostly
          | functional and declarative -- functions returning VNodes, and being diffed as in
          | React. For example, this is the code for rendering the gravity wells:
        """.stripMargin
      ),
      <("pre")(
        """
          |  /** Creates an SVG for a gravity well */
          |  def svgWell(w:Well):VNode = {
          |    val (x, y) = w.pos
          |
          |    <.circle(
          |      ^.cls := "well",
          |      ^.attr("cx") := x, ^.attr("cy") := y, ^.attr("r") := w.radius
          |    )
          |  }
          |
        """.stripMargin
      ),
      <.p(
        """
          | There is a single stateful component, SimulationView, that controls
          | starting, stopping, and editing the simulation. Unlike React, that component can
          | have its own `rerender()` method, so it is not necessary to regenerate the whole
          | UI on every tick. Though that could be done by calling `rerender()` on the router.
        """.stripMargin
      ),
      <.p(
        """
          | On Chrome, it seems to cope with around 250 asteroids before the framerate slows
          | below 60fps. Above 1,000 asteroids it judders a bit.
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
  case object SimulationView extends ElementComponent(<.div()) {

    override def afterAttach() = {
      super.afterAttach()
      Model.addListener(rerender)

      rerender()
    }

    override def beforeDetach() = Model.removeListener(rerender)

    var last:Long = System.currentTimeMillis()
    var dt:Long = 0

    // When we click reset, these parameters will be set on the model
    var asteroidCount = Model.count
    def reset(): Unit = {
      Model.count = asteroidCount
      Model.reset()
    }

    def rerender():Unit = {
      val now = System.currentTimeMillis()
      dt = now - last
      last = now

      renderElements(card(Model.asteroids))
    }

    def card(asteroids:Seq[Asteroid]) = {
      <.div(^.cls := "card",
        svg(
          Model.wells.map(svgWell) ++ Model.asteroids.map(svgAsteroid)
        ),
        <.div(^.cls := "card-footer",
          <.p(s"${asteroids.length} asteroids rendering in ${dt}ms"),
          <.div(^.cls := "btn-group",
            <("button")(
              ^.cls := "btn btn-sm btn-secondary", ^.onClick --> Model.stopTicking(),
              <("i")(^.cls := "fa fa-pause")
            ),
            <("button")(
              ^.cls := "btn btn-sm btn-secondary", ^.onClick --> Model.startTicking(),
              <("i")(^.cls := "fa fa-play")
            )
          ),
          <.div(^.cls := "input-group",
            <.span(^.cls := "input-group-addon", "Asteroids"),
            <("input")(^.attr("type") := "number", ^.cls := "form-control",
              ^.attr("value") := asteroidCount,
              ^.on("change") ==> { event => event.target match {
                case i:HTMLInputElement => asteroidCount = i.valueAsNumber
                case _ => // do nothing
              }}
            ),
            <.span(^.cls := "input-group-btn",
              <("button")(
                ^.cls := "btn btn-sm btn-secondary", ^.onClick --> reset, "Reset"
              )
            )
          )

        )
      )
    }
  }

}
