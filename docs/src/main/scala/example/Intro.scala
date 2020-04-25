package example

import com.wbillingsley.veautiful.html.{<, EventMethods, VHtmlComponent, VHtmlNode, ^}
import org.scalajs.dom
import org.scalajs.dom.{MouseEvent, Node}
import org.scalajs.dom.html.Canvas

object Intro {

  case class Greeter() extends VHtmlComponent {

    private var name:String = ""

    override def render = <.div(^.cls := "hello-world",
      <.input(
        ^.prop("value") := name,
        ^.attr("placeholder") := "Hello who?", ^.on("input") ==> { e => e.inputValue.foreach(name = _); rerender() }
      ),
      <.span(s" Hello ${ (if (name.isEmpty) "World" else name) }"),
    )

  }

  case class MouseTrails() extends VHtmlNode {

    private var _domNode:Option[Canvas] = None

    override def domNode: Option[Canvas] = _domNode

    var points:Seq[(Int, Int)] = Seq.empty

    /** Create the real node and make it accessible */
    override def attach(): Node = {
      val el = <.canvas(
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

  def page = Common.layout(<.div(
    <.p(^.cls := "logo",
      <.img(^.src := "veautiful-small.png"),
      <.p(^.cls := "logo-text", "Veautiful"),
      <.p(^.cls := "logo-slogan", "A devastatingly simple Scala.js front end")
    ),
    Common.markdown(
      """
        |Veautiful is a simple but effective web front end for Scala.js, written by [Will Billingsley](https://www.wbillingsley.com).
        |I wanted something flexible enough to write explorable explanations (teaching materials with embedded models and simulations)
        |but that would feel simpler to use than common frameworks such as React.
        |
        |As I have also written university courses in Scala and Advanced Web Programming, I also wanted its concepts to be
        |straightforward enough to teach to undergraduate students in less than a week.
        |
        |### Veautifully Built
        |
        |To see examples of the sorts of things that have been built with it, try one of the online course sites that have
        |various kinds of embedded models:
        |
        |* [Circuits Up](https://theintelligentbook.com/circuitsup) - embedded circuit simulations in a little course
        |  that teaches computer architecture from electronics up.
        |* [The Adventures of Will Scala](https://theintelligentbook.com/circuitsup) - a simpler site (mostly video
        |  and text) that goes alongside my undergraduate Scala course.
        |
        |### What's unique about Veautiful?
        |
        |Most frameworks make you choose between a Virtual DOM and direct updates. Veautiful lets your *components*
        |choose. In practice, this makes writing UIs that can have some very complex behaviours simpler and clearer.
        |
        |### Getting Veautiful
        |
        |Veautiful is published for Scala.js 1 and Scala 2.13 (dotty coming soon). While it is in snapshot, the easiest
        |way to get it is via jitpack
        |
        |```scala
        |resolvers += "jitpack" at "https://jitpack.io"
        |libraryDependencies ++= Seq(
        |  "org.scala-js" %%% "scalajs-dom" % "1.0.0",
        |  "com.github.wbillingsley.veautiful" %%% "veautiful" % "master-SNAPSHOT",
        |  "com.github.wbillingsley.veautiful" %%% "veautiful-templates" % "master-SNAPSHOT",
        |)
        |```
        |
        |### Hello World
        |
        |The `Attacher` connects to a DOM element, and can render a Veautiful tree into it:
        |
        |```scala
        |import com.wbillingsley.veautiful.html.{Attacher, <, ^}
        |import org.scalajs.dom
        |
        |val root = Attacher.newRoot(dom.document.getElementById("render-here"))
        |root.render(<.p("Hello world"))
        |```
        |
        |### Pure functions
        |
        |In many cases, components can just be functions that return a `VHtmlNode`. For example, from this documentation
        |site:
        |
        |```scala
        |def linkToRoute(r:ExampleRoute, s:String):VHtmlNode = <.a(
        |  ^.href := Router.path(r),
        |  ^.cls := (if (Router.route == r) "toc-link active" else "toc-link"),
        |  s
        |)
        |```
        |
        |Then you can just call that function in other component functions
        |
        |```scala
        |def leftMenu:VHtmlNode = <("nav")(^.cls := "d-none d-md-block",
        |  <.div(^.cls := "sidebar-sticky",
        |    <.ul(^.cls := "toc",
        |      for { (r, t) <- routes } yield <.li(
        |        ^.cls := "toc-item",
        |        linkToRoute(r, t)
        |      )
        |    )
        |  )
        |)
        |```
        |
        |The quoted code generates the table of contents links on the left side of this page.
        |
        |### Simple Mutable Components
        |
        |Many components that keep internal state can work simply in the style of a virtual DOM:
        |
        |""".stripMargin
    ),
    <.div(^.cls := "embedded-example",
      Greeter(),
      Common.markdown(
        """
          |```scala
          |import com.wbillingsley.veautiful.html.{<, VHtmlComponent, ^, EventMethods}
          |
          |case class Greeter() extends VHtmlComponent {
          |
          |  private var name:String = "World"
          |
          |  override def render = <.div(^.cls := "hello-world",
          |    <.input(
          |      ^.prop("value") := name,
          |      ^.attr("placeholder") := "Hello who?", ^.on("input") ==> { e => e.inputValue.foreach(name = _); rerender() }
          |    ),
          |    <.span(s" Hello ${ (if (name.isEmpty) "World" else name) }"),
          |  )
          |
          |}
          |```
          |""".stripMargin
      )
    ),
    Common.markdown(
      """
        |This will use the default reconciliation strategy to update its DOM tree in the page. This example has been written just
        |to re-render *this component*. We could go all the way up to re-rendering the Attacher if we want, however. Usually,
        |there's some component in the tree where it's obvious you'll want to trigger a re-renders &mdash; even if only the
        |site's router.
        |
        |Typically, components are implemented as `case classes`. This is not a strict requirement, but it makes things
        |much simpler to write because components that use a virtual DOM style (reconciling their children for updates)
        |use equality to determine whether a component needs to be replaced or can just be asked to reconcile itself.
        |Making your component a case class makes it simple and clear to show what would make your component "not equal"
        |to another instance of itself (and require replacement).
        |
        |### Go Crazy Components
        |
        |At the lowest level, a Veautiful UI is made up of `VNode[N]`s. These are JavaScript objects that can attach to
        |and control a node in the UI. When working with HTML, these control DOM nodes and elements, and we use the
        |type alias `VHtmlNode`.
        |
        |This means we can implement components as low-level nodes directly calling operations on their nodes.
        |It's a little more verbose, but not complicated.
        |For example, let's do a canvas widget that paints a mouse trail:
        |
        |""".stripMargin
    ),
    <.div(^.cls := "embedded-example",
      MouseTrails(),
      Common.markdown(
        """
          |```scala
          |import com.wbillingsley.veautiful.html.{VHtmlNode, <, ^}
          |import org.scalajs.dom
          |import org.scalajs.dom.{MouseEvent, Node}
          |import org.scalajs.dom.html.Canvas
          |
          |case class MouseTrails() extends VHtmlNode {
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
    )
  ))

}
