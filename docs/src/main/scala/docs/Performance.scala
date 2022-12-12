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

  val plot = new MutableArrayComponent[dom.Element, dom.Node, svg.Circle, (Double, Double)](
    <.svg(
      ^.attr("style") := "background: white; border-radius: 10px; margin-right: 15px;",
      ^.attr("width") := 100, ^.attr("height") := 100, ^.cls := "particles"
    ).build(), particles
  )(
    onEnter = { (d:(Double, Double), _) => SVG.circle(^.cls := "particle", ^.attr("r") := "1").build() },
    onUpdate = { (d:(Double, Double), i, v) =>
      for { circle <- v.domNode } {
        circle.setAttribute("cx", d._1.toInt.toString)
        circle.setAttribute("cy", d._2.toInt.toString)
      }
    }
  )

  override protected def render = {
    <.div(
      plot,
      if (animating) {
        <.button(^.cls := "btn btn-secondary", ^.onClick --> { animating = false; rerender() }, <("i")(^.cls := "fa fa-pause"))
      } else {
        <.button(^.cls := "btn btn-secondary", ^.onClick --> start(), <("i")(^.cls := "fa fa-play"))
      }
    )
  }
  
  override def afterAttach(): Unit = plot.update()
}

def performance = <.div(Common.markdown(
  """# Performance
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
    |To demonstrate how components can be quite different if they need to be, let's show a `MutableArrayComponent`.
    |This is an experimental little node, designed to work a little more like d3.js. It's not a full implementation (for that, just embed d3 in a
    |VNode), but it holds a data array, and *enter*, *exit*, and *update* sets for determining what to do with the child nodes.
    |
    |There are other ways of doing this reasonably efficiently in Veautiful, but perhaps sometimes the d3 style is clearer.
    |
    |In this case, we'll render 1,000 particles moving randomly in a small box.
    |The full code, including the play-pause button and the changes to the particle array, are in the demo box below,
    |but our d3-like component (the SVG containing the particles) looks like this:
    |
    |```scala
    |  val plot = new MutableArrayComponent[dom.Element, dom.Node, svg.Circle, (Double, Double)](
    |    <.svg(
    |      ^.attr("style") := "background: white; border-radius: 10px; margin: 15px;",
    |      ^.attr("width") := 100, ^.attr("height") := 100, ^.cls := "particles"
    |    ), particles
    |  )(
    |    onEnter = { (d:(Double, Double), _) => SVG.circle(^.cls := "particle", ^.attr("r") := "1") },
    |    onUpdate = { (d:(Double, Double), i, v) =>
    |      for { circle <- v.domNode } {
    |        circle.setAttribute("cx", d._1.toInt.toString)
    |        circle.setAttribute("cy", d._2.toInt.toString)
    |      }
    |    }
    |  )
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
        |    private val particles = Array.fill(1000)((Math.random() * 100, Math.random() * 100))
        |
        |    private var animating = false
        |
        |    def start() = {
        |      animating = true
        |      rerender()
        |      dom.window.requestAnimationFrame(_ => animationLoop())
        |    }
        |
        |    private def animationLoop():Unit = {
        |      updateParticles()
        |      if (animating) dom.window.requestAnimationFrame(_ => animationLoop())
        |    }
        |
        |    private def updateParticles():Unit = {
        |      for { i <- particles.indices } {
        |        val (x, y) = particles(i)
        |        particles(i) = (x + Math.random() * 2 - 1, y + Math.random() * 2 - 1)
        |      }
        |      plot.update()
        |    }
        |
        |    val plot = new MutableArrayComponent[dom.Element, dom.Node, svg.Circle, (Double, Double)](
        |      <.svg(), particles
        |    )(
        |      onEnter = { (d:(Double, Double), _) => SVG.circle(^.cls := "particle", ^.attr("r") := "1") },
        |      onUpdate = { (d:(Double, Double), i, v) =>
        |        for { circle <- v.domNode } {
        |          circle.setAttribute("cx", d._1.toInt.toString)
        |          circle.setAttribute("cy", d._2.toInt.toString)
        |        }
        |      }
        |    )
        |
        |    override protected def render = {
        |      <.div(
        |        plot,
        |        <.div(
        |          if (animating) {
        |            <.button(^.cls := "btn btn-secondary", ^.onClick --> { animating = false; rerender() }, <.i(^.cls := "fa fa-pause"))
        |          } else {
        |            <.button(^.cls := "btn btn-secondary", ^.onClick --> start(), <.i(^.cls := "fa fa-play"))
        |          }
        |        )
        |      )
        |    }
        |  }
        |
        |  override def afterAttach(): Unit = plot.update()
        |}
        |```
        |""".stripMargin)
  )
)

