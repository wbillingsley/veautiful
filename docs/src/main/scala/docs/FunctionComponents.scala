package docs

import com.wbillingsley.veautiful.html.{<, ^}
import org.scalajs.dom

def pureFunctions = <.div(
  Common.markdown(
    """
      |# Functions as components
      |
      |In many cases, reusable components can just be functions that return `VHtmlContent` (or `VSvgContent` for SVG). For example, from this documentation
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
      |def leftMenu:VHtmlContent = <.nav(^.cls := "d-none d-md-block",
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
      |The type we're producing with this little language (by default) is a *blueprint* for a `DHtmlElement`.
      |That is, we're producing an immutable description of what some VNodes should look like.
      |
      |If we want to create some mutable VNodes that control the DOM (rather than an immutable blueprint), we can do
      |so just by calling `<.mutable.p()` etc.
      |
      |This is a little like how in Scala there is an immutable collections API, but also a mutable collections API.
      |
      |""".stripMargin)
)