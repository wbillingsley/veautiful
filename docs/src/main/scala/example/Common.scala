package example

import com.wbillingsley.veautiful.html.{<, Markup, VHtmlNode, ^}

import scala.scalajs.js

/**
  * Common UI components to all the views
  */
object Common {

  val routes:Seq[(ExampleRoute, String)] = Seq(
    IntroRoute -> "Home",
    ToDoRoute -> "Example: To Do List",
    ReactLikeRoute -> "Example: Rendering asteroids into an SVG",
    DiffusionRoute -> "Example: Diffusion experiment",
    VSlidesRoute(0) -> "Example: VSlides",
    ScatterRoute -> "Example: Scatter",
  )

  def linkToRoute(r:ExampleRoute, s:String):VHtmlNode = <.a(
    ^.href := Router.path(r),
    ^.cls := (if (Router.route == r) "toc-link active" else "toc-link"),
    s
  )

  def leftMenu:VHtmlNode = <("nav")(^.cls := "d-none d-md-block",
    <.div(^.cls := "sidebar-sticky",
      <.ul(^.cls := "toc",
        for { (r, t) <- routes } yield <.li(
          ^.cls := "toc-item",
          linkToRoute(r, t)
        )
      )
    )
  )

  def layout(ch:VHtmlNode) = <.div(
    <.div(^.cls := "outer",
      <.div(^.cls := "sidebar", leftMenu),
      <.div(^.cls := "main",
        <.div(^.cls := "container", ch)
      )
    )
  )

  val markdownGenerator = new Markup({ s:String => js.Dynamic.global.marked(s).asInstanceOf[String] })

  def markdown(s:String):VHtmlNode = markdownGenerator.Fixed(s)


}
