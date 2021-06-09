package docs

import com.wbillingsley.veautiful.html.{<, ^}
import org.scalajs.dom

def pureFunctions = <.div(
  Common.markdown(
    """
      |# Function components
      |
      |In many cases, reusable components can just be functions that return a `VHtmlNode`. For example, from this documentation
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
      |## Functions not values
      |
      |You should always define these as *functions*, not *values*. The virtual HTML nodes you are creating are,
      |behind the scenes, *mutable*. When rendered into the document, they each attach themselves to a node within the page.
      |This means, for instance, that a single `<.div()` can only exist in one location within the page.
      |
      |By default, they can also be asked to morph themselves during the page update process. 
      |One `<.div()` that is being removed might instead be asked to update its content to match another `<.div()`. 
      |If you try to store a div in a `val`, you might be surprised to find it holding different children after the page update.
      |
      |We'll save the reasons why for the design section, but briefly, `<.div()` isn't a low-level node in the virtual DOM. 
      |It's a powerful component that declares it implements `MakeItSo` &mdash; a trait that indicates it can morph itself 
      |to match another component of the same type (another `<.div()`), rather than needing to be pulled out of the tree 
      |and replaced during a page update.
      |
      |Later on, we'll see some more unique things you can do with it - such as instructing one to change its update
      |strategy.
      |
      |*Caveat* - If you really do want to store one in a `val`, set its `^.key` to a unique value:
      | 
      |```scala
      |<.div(
      |  ^.key := "my special component 678a942",
      |  "Now this will only morph itself to match a div with the same key"
      |)
      |```
      |
      |It will only try to morph itself to match an element with the same tag and key. So, if the key is unique, you are
      |safe from other components accidentally modifying it and can keep it in a `val`.
      |""".stripMargin)
)