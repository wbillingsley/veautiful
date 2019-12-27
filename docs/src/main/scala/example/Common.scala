package example

import com.wbillingsley.veautiful.html.{<, VHtmlNode, ^}

/**
  * Common UI components to all the views
  */
object Common {

  val routes:Seq[(ExampleRoute, String)] = Seq(
    IntroRoute -> "Hello world",
    ToDoRoute -> "Example: To Do List",
    ReactLikeRoute -> "Example: Rendering asteroids into an SVG",
    DiffusionRoute -> "Example: Diffusion experiment",
    VSlidesRoute(0) -> "Example: VSlides",
    ScatterRoute -> "Example: Scatter",
    WrenRoute -> "Example: Wren",
    AssemblyRoute -> "Example: Assembly language sim"
  )

  def linkToRoute(r:ExampleRoute, s:String):VHtmlNode = <.a(
    ^.href := Router.path(r),
    ^.cls := (if (Router.route == r) "nav-link active" else "nav-link"),
    s
  )

  def leftMenu:VHtmlNode = <("nav")(^.cls := "d-none d-md-block bg-light sidebar",
    <.div(^.cls := "sidebar-sticky",
      <.ul(^.cls := "nav nav-pills flex-column",
        for { (r, t) <- routes } yield <.li(
          ^.cls := "nav-item",
          linkToRoute(r, t)
        )
      )
    )
  )

  def layout(ch:VHtmlNode) = <.div(
    <.div(^.cls := "row",
      <.div(^.cls := "col-sm-3", leftMenu),
      <.div(^.cls := "col-sm-9", ch)
    )
  )


}
