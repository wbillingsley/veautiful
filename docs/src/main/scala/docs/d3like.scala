package docs

import com.wbillingsley.veautiful.{DiffNode, MutableArrayComponent}
import com.wbillingsley.veautiful.html.{<, SVG, DHtmlComponent, VDomNode, ^}
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.{Element, MouseEvent, Node, svg}

object Particles extends DHtmlComponent {

  private val particles = Array.fill(1000)((Math.random() * 100, Math.random() * 100))

  private var animating = false

  def start() = {
    animating = true
    rerender()
    dom.window.requestAnimationFrame(_ => animationLoop())
  }

  private def animationLoop():Unit = {
    updateParticles()
    if (animating) dom.window.requestAnimationFrame(_ => animationLoop())
  }

  private def updateParticles():Unit = {
    for { i <- particles.indices } {
      val (x, y) = particles(i)
      particles(i) = (x + Math.random() * 2 - 1, y + Math.random() * 2 - 1)
    }
    plot.update()
  }

  import MutableArrayComponent._
  val plot = <.svg(
      ^.style := "background: white; border-radius: 10px; margin-right: 15px;",
      ^.attr.width := 100, ^.attr.height := 100, ^.cls := "particles"
    ).generateChildren(particles)(
      (_, _) => SVG.circle(^.cls := "particle", ^.attr.r := 1)
    ).onUpdate( (data, i, v) =>
      val (x, y) = data
      for { circle <- v.domNode } do 
        circle.setAttribute("cx", x.toString)
        circle.setAttribute("cy", y.toString)
    )


  override protected def render = {
    <.div(
      plot,
      if (animating) {
        <.button(^.cls := "btn btn-secondary", ^.onClick --> { animating = false; rerender() }, <.i(^.cls := "fa fa-pause"))
      } else {
        <.button(^.cls := "btn btn-secondary", ^.onClick --> start(), <.i(^.cls := "fa fa-play"))
      }
    )
  }
  
  override def afterAttach(): Unit = plot.update()
}

def d3like = <.div(Common.markdown(
  """# D3-like components
    |
    |Because VNodes are mutable, re-rendering can happen at any level of the tree - from the top level 
    |(often the router) as if you were using react, or as small as a local component of only one element.
    |
    |That makes most things fairly efficient. 
    |
    |However, we have a few more tricks up our sleaves.
    |
    |### Implementing different kinds of VNodes
    |
    |Because we can write our own mutable VNodes, we can make parts of the UI work very differently. For example, if we have
    |a thousand particles vibrating in a brownian motion simulation, we might not want to bother "declaratively" showing all 
    |1,000 particles inside the pseudo-HTML of our DSL language. We know what the particles are, so let's just create them.
    |
    |To demonstrate how components can be quite different if they need to be, let's show a node designed to work a little more like d3.js. 
    |It's not a full implementation (for that, just embed d3 in a VNode), but it holds a data array, and *enter*, *exit*, and *update* sets for determining what to do with the child nodes.
    |
    |There are other ways of doing this reasonably efficiently in Veautiful, but perhaps sometimes the d3 style is clearer.
    |
    |In this case, we'll render 1,000 particles moving randomly in a small box.
    |The full code, including the play-pause button and the changes to the particle array, are in the demo box below,
    |but our d3-like component (the SVG containing the particles) looks like this:
    |
    |```scala
    |  // Imports the "generateChildren" extension method
    |  import MutableArrayComponent._
    |
    |  // Create an SVG, with one circle child for every data point in the "particles" array
    |  val plot = <.svg(
    |      ^.style := "background: white; border-radius: 10px; margin-right: 15px;",
    |      ^.attr.width := 100, ^.attr.height := 100, ^.cls := "particles"
    |    ).generateChildren(particles)(
    |      // We have to give it a function to generate the children, equivalent to d3's enter function
    |      // It takes the data value and the index in the array, and should return a VNode. 
    |      // Let's just create an unplaced circle for each data item.
    |      (_, _) => SVG.circle(^.cls := "particle", ^.attr.r := 1)
    |    ).onUpdate( (data, i, v) =>
    |      // Equivalent to d3's update function
    |      // In-keeping with d3, this is also called for newly created elements as well as the existing ones.
    |      // Let's get the DOM node (the SVG circle) for the VNode and update its location
    |      val (x, y) = data
    |      for { circle <- v.domNode } do
    |        circle.setAttribute("cx", x.toString)
    |        circle.setAttribute("cy", y.toString)
    |    )
    |```
    |
    |""".stripMargin,
  ),
  <.div(^.cls := embeddedExampleStyle.className,
    Particles, <.p(),
    Common.markdown(
      """
        |```scala
        |import com.wbillingsley.veautiful.html.{VDomNode, <, ^}
        |import org.scalajs.dom
        |import org.scalajs.dom.{MouseEvent, Node}
        |import org.scalajs.dom.html.Canvas
        |
        |object Particles extends DHtmlComponent {
        |
        |  private val particles = Array.fill(1000)((Math.random() * 100, Math.random() * 100))
        |
        |  private var animating = false
        |
        |  def start() = {
        |    animating = true
        |    rerender()
        |    dom.window.requestAnimationFrame(_ => animationLoop())
        |  }
        |
        |  private def animationLoop():Unit = {
        |    updateParticles()
        |    if (animating) dom.window.requestAnimationFrame(_ => animationLoop())
        |  }
        |
        |  private def updateParticles():Unit = {
        |    for { i <- particles.indices } {
        |      val (x, y) = particles(i)
        |      particles(i) = (x + Math.random() * 2 - 1, y + Math.random() * 2 - 1)
        |    }
        |    plot.update()
        |  }
        |
        |  import MutableArrayComponent._
        |  val plot = <.svg(
        |      ^.style := "background: white; border-radius: 10px; margin-right: 15px;",
        |      ^.attr.width := 100, ^.attr.height := 100, ^.cls := "particles"
        |    ).generateChildren(particles)(
        |      (_, _) => SVG.circle(^.cls := "particle", ^.attr.r := 1)
        |    ).onUpdate( (data, i, v) =>
        |      val (x, y) = data
        |      for { circle <- v.domNode } do
        |        circle.setAttribute("cx", x.toString)
        |        circle.setAttribute("cy", y.toString)
        |    )
        |
        |
        |  override protected def render = {
        |    <.div(
        |      plot,
        |      if (animating) {
        |        <.button(^.cls := "btn btn-secondary", ^.onClick --> { animating = false; rerender() }, <.i(^.cls := "fa fa-pause"))
        |      } else {
        |        <.button(^.cls := "btn btn-secondary", ^.onClick --> start(), <.i(^.cls := "fa fa-play"))
        |      }
        |    )
        |  }
        |  
        |  override def afterAttach(): Unit = plot.update()
        |}
        |```
        |
        |A similar component is included for generating nodes from a mutable map (e.g. a JavaScript dictionary/map): `MutableMapComponent`
        |""".stripMargin)
  )
)

