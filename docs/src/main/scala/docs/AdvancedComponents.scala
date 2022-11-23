package docs

import com.wbillingsley.veautiful.{DiffNode, MutableArrayComponent}
import com.wbillingsley.veautiful.html.{<, SVG, DHtmlComponent, VDomNode, ^}
import org.scalajs.dom
import org.scalajs.dom.html.Canvas
import org.scalajs.dom.{Element, MouseEvent, Node, svg}

case class MouseTrails() extends VDomNode {

  private var _domNode:Option[Canvas] = None

  override def domNode: Option[Canvas] = _domNode

  var points:Seq[(Int, Int)] = Seq.empty

  /** Create the real node and make it accessible */
  override def attach(): Node = {
    val el = <.canvas(
      ^.attr("style") := "background: white; border-radius: 10px",
      ^.attr("width") := 200, ^.attr("height") := 100, ^.cls := "mousetrails",
      ^.on("mousemove") ==> { case e:MouseEvent =>
        update((e.clientX.toInt, e.clientY.toInt))
      }
    ).create()
    _domNode = Some(el)
    el
  }

  /** Remove the real node */
  override def detach(): Unit = {
    _domNode = None
  }

  /** Adds a point to the mouse trail and redraws */
  private def update(point:(Int, Int)): Unit = {
    points = (point +: points).take(10)

    for {
      c <- domNode
    } {
      val ctx = c.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
      val rect = c.getBoundingClientRect()

      ctx.fillStyle = "white"
      ctx.fillRect(0, 0, 200, 100)

      ctx.fillStyle = "#004479"
      for {
        ((x, y), i) <- points.zipWithIndex
      } {
        ctx.beginPath()
        ctx.arc(x - rect.left, y - rect.top, 10 - i, 0, 2 * Math.PI, true)
        ctx.fill()
      }
    }
  }
}

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
    ), particles
  )(
    onEnter = { (d:(Double, Double), _) => SVG.circle(^.cls := "particle", ^.attr("r") := "1") },
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

def advancedComponents = <.div(Common.markdown(
  """# Advanced Components
    |
    |At the lowest level, a Veautiful UI is made up of `VNode[N]`s. These are JavaScript objects that can attach to
    |and control a node in the UI. When working with HTML, these control DOM nodes and elements, and we use the
    |type alias `VDomNode`.
    |
    |This means we can implement components as low-level nodes directly calling operations on their nodes.
    |It's a little more verbose, but not complicated.
    |For example, let's do a canvas widget that paints a mouse trail:
    |
    |""".stripMargin),
  <.div(^.cls := embeddedExampleStyle.className,
    MouseTrails(),
    Common.markdown(
      """
        |```scala
        |import com.wbillingsley.veautiful.html.{VDomNode, <, ^}
        |import org.scalajs.dom
        |import org.scalajs.dom.{MouseEvent, Node}
        |import org.scalajs.dom.html.Canvas
        |
        |case class MouseTrails() extends VDomNode {
        |
        |    private var _domNode:Option[Canvas] = None
        |
        |    override def domNode: Option[Canvas] = _domNode
        |
        |    var points:Seq[(Int, Int)] = Seq.empty
        |
        |    /** Create the real node and make it accessible */
        |    override def attach(): Node = {
        |      val el = <.canvas(
        |        ^.attr("style") := "background: white; border-radius: 10px",
        |        ^.attr("width") := 200, ^.attr("height") := 100, ^.cls := "mousetrails",
        |        ^.on("mousemove") ==> { case e:MouseEvent =>
        |          update((e.clientX.toInt, e.clientY.toInt))
        |        }
        |      ).create()
        |      _domNode = Some(el)
        |      el
        |    }
        |
        |    /** Remove the real node */
        |    override def detach(): Unit = {
        |      _domNode = None
        |    }
        |
        |    /** Adds a point to the mouse trail and redraws. This looks long because it's canvas code. */
        |    private def update(point:(Int, Int)): Unit = {
        |      points = (point +: points).take(10)
        |
        |      for {
        |        c <- domNode
        |      } {
        |        val ctx = c.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]
        |        val rect = c.getBoundingClientRect()
        |
        |        ctx.fillStyle = "white"
        |        ctx.fillRect(0, 0, 200, 100)
        |
        |        ctx.fillStyle = "#004479"
        |        for {
        |          ((x, y), i) <- points.zipWithIndex
        |        } {
        |          ctx.beginPath()
        |          ctx.arc(x - rect.left, y - rect.top, 10 - i, 0, 2 * Math.PI, true)
        |          ctx.fill()
        |        }
        |      }
        |    }
        |  }
        |```
        |""".stripMargin)
  ),
  Common.markdown(
    """
      |### Example: Mutable Array Components
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
        |            <.button(^.cls := "btn btn-secondary", ^.onClick --> { animating = false; rerender() }, <("i")(^.cls := "fa fa-pause"))
        |          } else {
        |            <.button(^.cls := "btn btn-secondary", ^.onClick --> start(), <("i")(^.cls := "fa fa-play"))
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
