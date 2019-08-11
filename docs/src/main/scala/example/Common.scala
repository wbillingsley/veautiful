package example

import com.wbillingsley.veautiful.{<, VNode, ^}

/**
  * Common UI components to all the views
  */
object Common {

  val routes:Seq[(ExampleRoute, String)] = Seq(
    IntroRoute -> "Hello world",
    ToDoRoute -> "Example: To Do List",
    ReactLikeRoute -> "Example: Rendering asteroids into an SVG"
  )

  def linkToRoute(r:ExampleRoute, s:String):VNode = <.a(
    ^.href := Router.path(r),
    ^.cls := (if (Router.route == r) "nav-link active" else "nav-link"),
    s
  )

  def leftMenu:VNode = <.div(
    <.h3("Veautiful"),
    <.ul(^.cls := "nav nav-pills flex-column",
      for { (r, t) <- routes } yield <.li(
        ^.cls := "nav-item",
        linkToRoute(r, t)
      )
    )
  )

  def layout(ch:VNode) = <.div(
    ^.cls := "row",
    <.div(^.cls := "col-sm-4 sidebar", leftMenu),
    <.div(^.cls := "col-sm-8", ch)
  )


}
