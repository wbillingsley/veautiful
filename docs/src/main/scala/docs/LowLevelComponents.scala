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
    ).build().create()
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


def lowlevelComponents = <.div(Common.markdown(
  """# Low level components
    |
    |Veautiful is a mixed-paradigm toolkit that tries to be reasonably close to the metal in terms of its low level components,
    |but makes declarative styles of UI simple. 
    |
    |### The fundamental problem of the Web
    |
    |The fundamental problem of the web, as I see it, is that we have a markup language that looks declarative 
    |(HTML) that has to be mutable because there is a lot of transient state - e.g. scroll positions, where were up to in a video, etc.
    |
    |So, at the lowest level, we're trying to enable two kinds of working:
    |
    |* mutable manipulation of elements, and
    |* declarative declaration of what the UI should look like.
    |
    |Fortunately, that is fairly idiomatic for Scala. It doesn't prevent mutation; it just makes functional code easy to write.
    |
    |### Mutable VNodes
    |
    |At the lowest level, a Veautiful UI is made up of `VNode[N]`s. These are JavaScript objects that own
    |and control a node in the UI. When working with HTML, these control DOM nodes and elements, and we use
    |type aliases like `VDomNode`, `VHtmlElement`, or `VSvgElement`.
    |
    |A `VNode` has two states
    |
    |* *Attached* - that is, it is controlling a DOM node 
    |* *Umattached* - it's not controlling a DOM node in the tree, and can be passed around like a cheap object
    |
    |`VNode`s go through a very traditional UI lifecycle:
    |
    |1. creation
    |2. before attach / mount
    |3. attach / mount
    |4. after attach / mount
    |5. before detach / unmount
    |6. detach / unmount
    |7. after detach / unmount
    |
    |We can reattach them if we want too, of course.
    |
    |Effectively, this gives us a tree of View Controllers. But we can pass around trees of *unattached* View Controllers
    |as if they were a virtual DOM whenever we want.
    |
    |## Manually implementing a mutable VNode
    |
    |This means we can implement components as low-level `VNode`s by directly calling operations on their nodes.
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
        |    override def attach() = {
        |      val el = <.canvas(
        |        ^.style := "background: white; border-radius: 10px",
        |        ^.attr("width") := 200, ^.attr("height") := 100, ^.cls := "mousetrails",
        |        ^.onMouseMove ==> { e =>
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
      |## Direct nodes
      |
      |More commonly, we might have some JavaScript that produces a real DOM node (e.g. from another library) and we need to incorporate that
      |node into the tree.
      |
      |There's a helper class for this called `DirectElement`
      |
      |```scala
      |import com.wbillingsley.veautiful
      |import veautiful.html.<
      |
      |val domElement:dom.html.Element = somethingThatReturnsAReadDOMElement()
      |
      |val directElement = DirectElement(domElement)
      |
      |// I can now use this in a render tree
      |val myFragment = <.div(
      |  <.h1("Here it is"),
      |  directElement
      |)
      |```
      |
      |We can also extend `DirectElement` if we want to manipulate the element ourselves. This isn't a hack; it is idiomatic in Veautiful.
      |The role of a `VNode` is to *own and control* an element in the tree.
      |
      |### Performance
      |
      |Being able to create low level nodes is useful for performance. Sometimes, I do have simulations of a thousand particles moving at once.
      |In those cases, it can be helpful to know that you can write a mutable VNode for it and not worry a jot about the performance of reconcilers,
      |dynamic bindings, or other high level fun.
      |
      |But for everyday work, it's often nicer to use something declarative and high level. Veautiful UIs are similar: most of the code we write is 
      |declarative, but when we need to we can insert some mutation.
      |
      |""".stripMargin)
  )

