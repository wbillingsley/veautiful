package docs

import com.wbillingsley.veautiful.html.{<, ^}
import org.scalajs.dom

def pureFunctions = <.div(
  Common.markdown(
    """
      |# Functions as components
      |
      |In many cases, reusable components can just be functions that return `DHtmlContent` (or `DSvgContent` for SVG). For example, from this documentation
      |site:
      |
      |```scala
      |def linkToRoute(r:ExampleRoute, s:String):VHtmlContent = <.a(
      |  ^.href := Router.path(r),
      |  ^.cls := (if (Router.route == r) "toc-link active" else "toc-link"),
      |  s
      |)
      |```
      |
      |Then you can just call that function in other component functions
      |
      |```scala
      |def leftMenu:DHtmlContent = <.nav(^.cls := "d-none d-md-block",
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
      |### Until 0.3-M4, prefer functions to values 
      |
      |**Until 0.3-M4**, you should define these as functions, rather than values. 
      |
      |Each `DHtmlElement`, when it is mounted in the page, "owns" a single DOM element. That means it can only appear once in the DOM tree.
      |It's also intentionally mutable. A `DHtmlElement`'s key skill is that it can morph itself to match a target. This helps us to 
      |declare our components and views in a declarative style, just stating what their output should look like, but still preserve as
      |many of the real nodes in the DOM (with all their ephemeral state, like scroll position and loaded videos).
      |
      |By design, `DHtmlElement`s declare that their retention strategy is to morph them to match any element with the same tag name.
      |
      |However, that mutability can come as a surprise if you've said something like
      |
      |```scala
      |val myPage1 = <.article(
      |  <.h1("This is page 1")
      |)
      |```
      |
      |only to find out later the `DHtmlElement` in the `myPage1` val has morphed itself to match page 2.
      |
      |If you do want to store one in a `val` rather than a `def`, though, there's a helper for that
      | 
      |```scala
      |Unique(<.div(
      |  "This node will only be considered equivalent to itself; it won't morph to match any other div"
      |))
      |```
      |
      |### From 0.3-M4, you can just use `val`
      |
      |The next release will switch the HTML DSL over so that instead of producing real `DHtmlElement`s, it 
      |produces immutable `Blueprint`s for them (`DHtmlBlueprint`). As of 0.3-M2, we can ask elements
      |to morph themselves to match either another element or a blueprint for one. In 0.3-M4, the HTML
      |DSL will produce Blueprints by default.
      |
      |This is not a source compatible change. In the locations where you have put a `DHtmlElement` into a 
      |`val`, you'll get a type error as it gives you a `DHtmlBlueprint` instead. You can fix it by calling
      |`.build()` on the blueprint to get a real `DHtmlElement`.
      |
      |The type `DHtmlContent`, that you can see in the first example in the page, is a type alias for
      |`DHtmlElement | DHtmlBlueprint`. That code example won't be affected - it'll just change which side of
      |the union type it is producing.
      |
      |""".stripMargin)
)